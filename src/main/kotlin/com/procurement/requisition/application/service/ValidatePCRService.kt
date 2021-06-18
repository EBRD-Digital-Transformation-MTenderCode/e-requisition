package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.SpecificWeightedPrice.Model.CriteriaMatrix
import com.procurement.requisition.application.service.SpecificWeightedPrice.Model.Criterion
import com.procurement.requisition.application.service.SpecificWeightedPrice.Model.RequirementGroup
import com.procurement.requisition.application.service.SpecificWeightedPrice.Model.Requirements
import com.procurement.requisition.application.service.SpecificWeightedPrice.Operations.Combination
import com.procurement.requisition.application.service.SpecificWeightedPrice.Operations.buildRequirementsMatrix
import com.procurement.requisition.application.service.SpecificWeightedPrice.Operations.getAllRequirementsCombinations
import com.procurement.requisition.application.service.error.ValidatePCRErrors
import com.procurement.requisition.application.service.model.command.ValidatePCRDataCommand
import com.procurement.requisition.application.service.rule.RulesService
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.failure.incident.InvalidArgumentValueIncident
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.dataType
import com.procurement.requisition.domain.model.isNotUniqueIds
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.isDataTypeMatched
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality.ELECTRONIC_AUCTION
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality.REQUIRES_ELECTRONIC_CATALOGUE
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionCategory
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.infrastructure.handler.v2.converter.extension.checkForBlank
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import com.procurement.requisition.lib.isUnique
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ValidatePCRService(
    private val rulesService: RulesService,
) {

    fun validate(command: ValidatePCRDataCommand): Validated<Failure> {
        validateTextAttributes(command).onFailure { return it }
        // VR.COM-17.1.1
        if (command.tender.lots.isNotUniqueIds())
            return ValidatePCRErrors.Lot.DuplicateId().asValidatedError()

        command.tender.lots
            .forEach { lot ->
                // VR.COM-17.1.2
                validateLotClassification(
                    tenderClassification = command.tender.classification,
                    lotClassification = lot.classification
                ).onFailure { return it }

                if (!lot.classification.equalsId(command.tender.classification, 4))
                    return ValidatePCRErrors.Lot.InvalidClassificationId().asValidatedError()

                // VR.COM-17.1.3
                lot.variants
                    .forEach { variant ->
                        if (!variant.hasVariants && variant.variantsDetails != null)
                            return ValidatePCRErrors.Lot.VariantsDetails(lot.id).asValidatedError()
                    }

            }

        // VR.COM-17.1.4
        if (command.tender.items.isNotUniqueIds())
            return ValidatePCRErrors.Item.DuplicateId().asValidatedError()

        val lotIds = command.tender.lots.toSet { it.id }

        command.tender.items
            .forEach { item ->
                // VR.COM-17.1.5
                validateItemClassification(
                    tenderClassification = command.tender.classification,
                    itemClassification = item.classification
                ).onFailure { return it }

                // VR.COM-17.1.6
                if (item.quantity <= BigDecimal.ZERO)
                    return ValidatePCRErrors.Item.InvalidQuantity().asValidatedError()

                // VR.COM-17.1.7
                if (item.relatedLot !in lotIds)
                    return ValidatePCRErrors.Item.InvalidRelatedLot().asValidatedError()
            }

        // VR.COM-17.1.29
        val itemsByRelatedLot = command.tender.items.toSet { it.relatedLot }
        if (REQUIRES_ELECTRONIC_CATALOGUE in command.tender.procurementMethodModalities) {
            command.tender.lots.forEach { lot ->
                if (lot.id !in itemsByRelatedLot)
                    return ValidatePCRErrors.Lot.MissingItem().asValidatedError()
            }
        }

        val itemsById = command.tender.items.associateBy { it.id }

        // VR.COM-17.1.8
        if (command.tender.targets.isNotUniqueIds())
            return ValidatePCRErrors.Target.DuplicateId().asValidatedError()
        command.tender.targets
            .forEachIndexed { targetIdx, target ->

                // VR.COM-17.1.9
                when (target.relatesTo) {
                    TargetRelatesTo.ITEM -> if (target.relatedItem !in itemsById)
                        return ValidatePCRErrors.Target.InvalidRelatedItem().asValidatedError()

                    TargetRelatesTo.LOT -> if (target.relatedItem !in lotIds)
                        return ValidatePCRErrors.Target.InvalidRelatedItem().asValidatedError()
                }

                // VR.COM-17.1.10
                if (target.observations.isNotUniqueIds())
                    return ValidatePCRErrors.Target.Observation.DuplicateId(
                        path = "#/tender/targets[$targetIdx]/observations"
                    ).asValidatedError()

                target.observations
                    .forEachIndexed { observationIdx, observation ->
                        // VR.COM-17.1.11
                        observation.period
                            ?.apply {
                                if (startDate != null && endDate != null) {
                                    if (!startDate.isBefore(endDate))
                                        return ValidatePCRErrors.Target.Observation.InvalidPeriod(
                                            path = "#/tender/targets[$targetIdx]/observations[$observationIdx]",
                                            startDate = startDate,
                                            endDate = endDate
                                        ).asValidatedError()
                                }
                            }
                    }
            }

        if (command.tender.criteria.isNotUniqueIds())
            return ValidatePCRErrors.Criterion.DuplicateId(path = "#/tender/criteria").asValidatedError()

        // VR.COM-17.1.16
        validationRequirementGroupIds(command.tender.criteria).onFailure { return it }

        // VR.COM-17.1.17
        validationRequirementIds(command.tender.criteria).onFailure { return it }

        // VR.COM-17.1.37
        val isCriteriaNeed = isCriteriaNeed(command.tender.awardCriteria)
        if (isCriteriaNeed) validateCriteriaExistence(command.tender.criteria).onFailure { return it }

        //VR.COM-17.1.44, VR.COM-17.1.45
        checkEligibleEvidences(command).onFailure { return it }

        val mdmCriteriaByClassificationId = command.mdm.criteria.associateBy { it.classification.id }
        command.tender.criteria
            .forEachIndexed { criterionIdx, criterion ->

                if (criterion.isOther()) {
                    val foundedCriterion = mdmCriteriaByClassificationId[criterion.classification.id]
                        ?:  return ValidatePCRErrors.Criterion.CriteriaClassificationIdMismatch("#/tender/criteria[$criterionIdx]")
                            .asValidatedError()

                    if (foundedCriterion.classification.scheme != criterion.classification.scheme)
                        return ValidatePCRErrors.Criterion.CriteriaClassificationSchemeMismatch("#/tender/criteria[$criterionIdx]")
                            .asValidatedError()
                } else {
                    return ValidatePCRErrors.Criterion.MissingOtherCriteria("#/tender/criteria[$criterionIdx]")
                        .asValidatedError()
                }

                //VR.COM-17.1.31
                if (criterion.isRelatesToLotOrItem() && criterion.relatedItem == null)
                    return ValidatePCRErrors.Criterion.MissingRelatedItem("#/tender/criteria[$criterionIdx]")
                        .asValidatedError()

                // VR.COM-17.1.15
                if (criterion.isRelatesToTender() && criterion.relatedItem != null)
                    return ValidatePCRErrors.Criterion.UnknownAttributeRelatedItem()
                        .asValidatedError()

                if (criterion.relatedItem != null) {
                    // VR.COM-17.1.14
                    when (criterion.relatesTo) {
                        CriterionRelatesTo.ITEM -> if (criterion.relatedItem !in itemsById)
                            return ValidatePCRErrors.Criterion.InvalidRelatedItem(
                                path = "#/tender/criteria[$criterionIdx]/relatesTo",
                                relatedItem = criterion.relatedItem
                            ).asValidatedError()

                        CriterionRelatesTo.LOT -> if (criterion.relatedItem !in lotIds)
                            return ValidatePCRErrors.Criterion.InvalidRelatedItem(
                                path = "#/tender/criteria[$criterionIdx]/relatesTo",
                                relatedItem = criterion.relatedItem
                            ).asValidatedError()

                        CriterionRelatesTo.TENDER,
                        CriterionRelatesTo.TENDERER,
                        CriterionRelatesTo.AWARD -> InvalidArgumentValueIncident(
                            name = "relatesTo",
                            value = criterion.relatesTo,
                            expectedValue = listOf(CriterionRelatesTo.ITEM, CriterionRelatesTo.LOT)
                        ).asValidatedError()
                    }
                }

                criterion.requirementGroups
                    .forEachIndexed { requirementGroupIdx, requirementGroup ->
                        requirementGroup.requirements
                            .forEachIndexed { requirementIdx, requirement ->
                                // VR.COM-17.1.18
                                requirement.period
                                    ?.apply {
                                        if (startDate.isEqual(endDate) || startDate.isAfter(endDate))
                                            return ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidPeriod(
                                                path = "#/tender/requirementGroups[$requirementGroupIdx]/requirements[$requirementIdx]",
                                                startDate = startDate,
                                                endDate = endDate
                                            ).asValidatedError()

                                    }

                                // VR.COM-17.1.19-VR.COM-17.1.21, VR.COM-17.1.32-VR.COM-17.1.36
                                requirement.validateValueAttributes()
                                    .onFailure { return it }
                            }
                    }
            }

        // VR.COM-17.1.39
        checkMinSpecificWeightedPrice(command).onFailure { return it }

        // VR.COM-17.1.22
        if (command.tender.conversions.isNotUniqueIds())
            return ValidatePCRErrors.Conversion.DuplicateId(path = "#/tender/conversions").asValidatedError()

        val requirementIds = command.tender.criteria.asSequence()
            .flatMap { criterion -> criterion.requirementGroups.asSequence() }
            .flatMap { requirementGroup -> requirementGroup.requirements.asSequence() }
            .map { requirement -> requirement.id to requirement }
            .toMap()

        // VR.COM-17.1.38
        val isConversionsNeed = isConversionsNeed(command.tender.awardCriteria)
        if (!isConversionsNeed) validateConversionsNotExists(command.tender.conversions).onFailure { return it }

        // VR.COM-17.1.24
        if (command.tender.conversions.flatMap { it.coefficients }.isNotUniqueIds())
            return ValidatePCRErrors.Conversion.Coefficient.DuplicateId(
                path = "#/tender/conversions/coefficients"
            ).asValidatedError()

        command.tender.conversions
            .forEachIndexed { conversionIdx, conversion ->
                // VR.COM-17.1.23
                if (conversion.relatedItem !in requirementIds)
                    return ValidatePCRErrors.Conversion.InvalidRelatedItem(
                        path = "#/tender/conversions[$conversionIdx]",
                        relatedItem = conversion.relatedItem
                    ).asValidatedError()

                //VR.COM-17.1.30
                if (!conversion.coefficients.isUnique { it.value })
                    return ValidatePCRErrors.Conversion.Coefficient.DuplicateValue(
                        path = "#/tender/conversions[$conversionIdx]/coefficients"
                    ).asValidatedError()

                val requirement: ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement =
                    requirementIds.getValue(conversion.relatedItem)
                conversion.coefficients
                    .forEachIndexed { coefficientIdx, coefficient ->
                        // VR.COM-17.1.25
                        coefficient.matchingDataType(
                            path = "#/tender/conversions[$conversionIdx]/coefficients[$coefficientIdx]",
                            dataType = requirement.dataType
                        ).onFailure { return it }
                    }
            }

        command.tender.targets
            .forEachIndexed { targetIdx, target ->
                target.observations
                    .forEachIndexed { observationIdx, observation ->
                        // VR.COM-17.1.12
                        if (observation.relatedRequirementId == null || observation.relatedRequirementId in requirementIds)
                            Unit
                        else
                            return ValidatePCRErrors.Target.Observation.InvalidRelatedRequirementId(
                                path = "#/tender/targets[${targetIdx}]/observations[$observationIdx]",
                                relatedRequirementId = observation.relatedRequirementId
                            ).asValidatedError()
                    }
            }

        // VR.COM-17.1.26
        if (command.tender.documents.isNotUniqueIds())
            return ValidatePCRErrors.Document.DuplicateId(path = "#/tender/documents").asValidatedError()

        command.tender.documents
            .forEachIndexed { documentIdx, document ->
                // VR.COM-17.1.27
                if (document.relatedLots.isNotEmpty())
                    document.relatedLots.forEachIndexed { relatedLotIdx, relatedLot ->
                        if (relatedLot !in lotIds)
                            return ValidatePCRErrors.Document.InvalidRelatedLot(
                                path = "#/tender/documents[$documentIdx]/[$relatedLotIdx]",
                                relatedLot = relatedLot
                            ).asValidatedError()
                    }
            }

        // VR.COM-17.1.28
        if (command.tender.procurementMethodModalities.size > 1)
            return ValidatePCRErrors.ProcurementMethodModality.MultiValue().asValidatedError()

        //VR.COM-17.1.40
        checkAwardCriteriaDetails(command.tender).onFailure { return it }

        //VR.COM-17.1.46, VR.COM-17.1.47
        checkElectronicAuction(command.tender).onFailure { return it }


        return Validated.ok()
    }

    private fun validateTextAttributes(command: ValidatePCRDataCommand): Validated<Failure> {
        command.tender.apply {
            title.checkForBlank("tender.title").onFailure { return it }
            description.checkForBlank("tender.description").onFailure { return it }

            lots.mapIndexed{lotIndex, lot ->
                lot.internalId.checkForBlank("tender.lots[$lotIndex].internalId")
                    .onFailure { return it }

                lot.title.checkForBlank("tender.lots[$lotIndex].internalId")
                    .onFailure { return it }

                lot.description.checkForBlank("tender.lots[$lotIndex].description")
                    .onFailure { return it }

                lot.variants.mapIndexed { variantIndex, variant ->
                    variant.variantsDetails.checkForBlank("tender.lots[$lotIndex].variants[$variantIndex].variantsDetails")
                        .onFailure { return it }
                }
            }
            items.mapIndexed { itemIndex, item ->
                item.internalId.checkForBlank("tender.items[$itemIndex].internalId")
                    .onFailure { return it }

                item.description.checkForBlank("tender.items[$itemIndex].description")
                    .onFailure { return it }
            }
            targets.mapIndexed { targetIndex, target ->
                target.id.checkForBlank("tender.targets[$targetIndex].description")
                    .onFailure { return it }

                target.title.checkForBlank("tender.targets[$targetIndex].description")
                    .onFailure { return it }

                target.observations.mapIndexed { observationIndex, observation ->
                    observation.notes.checkForBlank("tender.targets[$targetIndex].observations[$observationIndex].notes")
                        .onFailure { return it }

                    observation.id.checkForBlank("tender.targets[$targetIndex].observations[$observationIndex].id")
                        .onFailure { return it }

                    observation.dimensions?.apply {
                        requirementClassIdPR.checkForBlank("tender.targets[$targetIndex].observations[$observationIndex].dimensions")
                            .onFailure { return it }
                    }
                }
            }
            criteria.mapIndexed { criteriaIndex, criterion ->
                criterion.description.checkForBlank("tender.criteria[$criteriaIndex].description")
                    .onFailure { return it }

                criterion.id.checkForBlank("tender.criteria[$criteriaIndex].id")
                    .onFailure { return it }

                criterion.title.checkForBlank("tender.criteria[$criteriaIndex].title")
                    .onFailure { return it }

                criterion.requirementGroups.mapIndexed { requirementGroupIndex, requirementGroup ->
                    requirementGroup.description.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].description")
                        .onFailure { return it }

                    requirementGroup.id.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].id")
                        .onFailure { return it }
                    requirementGroup.requirements.mapIndexed { requirementIndex, requirement ->
                        requirement.description.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].requirements[$requirementIndex].description")
                            .onFailure { return it }

                        requirement.title.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].requirements[$requirementIndex].title")
                            .onFailure { return it }

                        requirement.eligibleEvidences.mapIndexed { eligibleEvidenceIndex, eligibleEvidence ->
                            eligibleEvidence.id.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].requirements[$requirementIndex].eligibleEvidences[$eligibleEvidenceIndex].id")
                                .onFailure { return it }

                            eligibleEvidence.title.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].requirements[$requirementIndex].eligibleEvidences[$eligibleEvidenceIndex].title")
                                .onFailure { return it }

                            eligibleEvidence.description.checkForBlank("tender.criteria[$criteriaIndex].requirementGroups[$requirementGroupIndex].requirements[$requirementIndex].eligibleEvidences[$eligibleEvidenceIndex].description")
                                .onFailure { return it }
                        }
                    }
                }
            }
            conversions.mapIndexed { conversionIndex, conversion ->
                conversion.rationale.checkForBlank("tender.conversions[$conversionIndex].rationale")
                    .onFailure { return it }

                conversion.description.checkForBlank("tender.conversions[$conversionIndex].description")
                    .onFailure { return it }

                conversion.id.checkForBlank("tender.conversions[$conversionIndex].id")
                    .onFailure { return it }
            }
            documents.mapIndexed { documentIndex, document ->
                document.title.checkForBlank("tender.documents[$documentIndex].title")
                    .onFailure { return it }

                document.description.checkForBlank("tender.documents[$documentIndex].description")
                    .onFailure { return it }
            }
        }

        return Validated.ok()
    }

    private fun checkEligibleEvidences(command: ValidatePCRDataCommand): Validated<Failure> {
        val eligibleEvidences = command.tender.criteria
            .asSequence()
            .flatMap { it.requirementGroups }
            .flatMap { it.requirements }
            .flatMap { it.eligibleEvidences }
            .toList()

        checkUniqueness(eligibleEvidences)
            .onFailure { return it }

        checkForMissingDocuments(command, eligibleEvidences)
            .onFailure { return it }

        return Validated.ok()
    }

    private fun checkForMissingDocuments(
        command: ValidatePCRDataCommand,
        eligibleEvidences: List<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence>
    ): Validated<Failure> {
        val documents = command.tender.documents.toSet { it.id }
        val eligibleEvidencesDocuments = eligibleEvidences.mapNotNull { it.relatedDocument?.id }.toSet()
        val missingDocuments = eligibleEvidencesDocuments - documents

        return if (missingDocuments.isNotEmpty())
            ValidatePCRErrors.Criterion.RequirementGroup.Requirement.EligibleEvidence.MissingDocuments(missingDocuments)
                .asValidatedError()
        else Validated.ok()
    }

    private fun checkUniqueness(eligibleEvidences: List<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence>) =
        if (eligibleEvidences.isNotUniqueIds()) {
            ValidatePCRErrors.Criterion.RequirementGroup.Requirement.EligibleEvidence.DuplicateId("#/tender/criteria/requirementGroups/requirements/eligibleEvidences")
                .asValidatedError()
        } else
            Validated.ok()


    private fun checkAwardCriteriaDetails(tender: ValidatePCRDataCommand.Tender) =
        if (tender.awardCriteria == AwardCriteria.PRICE_ONLY
            && tender.awardCriteriaDetails != AwardCriteriaDetails.AUTOMATED)
            ValidatePCRErrors.AwardCriteriaDetails.InvalidValue(tender.awardCriteriaDetails.toString())
                .asValidatedError()
        else Validated.ok()

    private fun checkElectronicAuction(tender: ValidatePCRDataCommand.Tender): Validated<Failure> {
        when (tender.mustContainElectronicAuctions()) {
            true -> if (tender.electronicAuctions == null)
                return ValidatePCRErrors.ElectronicAuctions.MissingElectronicAuctions().asValidatedError()
            false -> if (tender.electronicAuctions != null)
                return ValidatePCRErrors.ElectronicAuctions.RedundantElectronicAuctions().asValidatedError()
        }
        return Validated.ok()
    }

    private fun ValidatePCRDataCommand.Tender.mustContainElectronicAuctions() =
        procurementMethodModalities.contains(ELECTRONIC_AUCTION)

    fun checkMinSpecificWeightedPrice(command: ValidatePCRDataCommand): Validated<Failure> {
        val minSpecificWeightPrice = rulesService
            .getMinSpecificWeightPrice(country = command.country, pmd = command.pmd)
            .map { it.valueOf(command.tender.mainProcurementCategory) }
            .onFailure { return it.reason.asValidatedError() }

        val itemsWithRelatedLot = command.tender.items.map { it.id to it.relatedLot }
        val lotsIds = command.tender.lots.map { it.id }
        val criteriaPackageByLot = getCriteriaPackageByLot(lotsIds, itemsWithRelatedLot, command.tender.criteria)

        criteriaPackageByLot.forEach { criteria ->
            val matrix = buildRequirementsMatrix(criteria)
            val allRequirementsCombinations = getAllRequirementsCombinations(matrix)
            val minCoefficientForRequirements = getMinCoefficients(command.tender.conversions)

            allRequirementsCombinations
                .forEach { combination ->
                    val specificWeightPrice = calculateSpecificWeightPrice(combination, minCoefficientForRequirements)
                    if (specificWeightPrice < minSpecificWeightPrice)
                        return ValidatePCRErrors.Criterion.TooSmallSpecificWeightPrice(combination).asValidatedError()
                }
        }

        return Validated.ok()
    }

    companion object {

        /**
         * VR.COM-17.1.19
         */
        fun ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.validateValueAttributes(): Validated<Failure> {
            when {
                Requirement.hasOnlyExpectedValue(expectedValue, minValue, maxValue) ->
                    validateOnlyExpectedValue(expectedValue!!, dataType).onFailure { return it }

                Requirement.hasOnlyMinValue(expectedValue, minValue, maxValue) ->
                    validateOnlyMinValue(minValue!!, dataType).onFailure { return it }

                Requirement.hasOnlyMaxValue(expectedValue, minValue, maxValue) ->
                    validateOnlyMaxValue(maxValue!!, dataType).onFailure { return it }

                Requirement.hasRangeValue(expectedValue, minValue, maxValue) ->
                    validateRange(minValue!!, maxValue!!, dataType).onFailure { return it }

                Requirement.valueNotBounded(expectedValue, minValue, maxValue) -> Unit

                else ->
                    return ValidatePCRErrors.Criterion.RequirementGroup.Requirement.WrongValueAttributesCombination(id = this.id)
                        .asValidatedError()
            }
            return Validated.ok()
        }

        fun validateOnlyExpectedValue(
            expectedValue: ExpectedValue,
            dataType: DynamicValue.DataType
        ): Validated<Failure> {
            expectedValue.checkDataType().onFailure { return it }
            expectedValue.matchingDataType(dataType).onFailure { return it }
            return Validated.ok()
        }

        fun validateOnlyMinValue(minValue: MinValue, dataType: DynamicValue.DataType): Validated<Failure> {
            minValue.checkDataType().onFailure { return it }
            minValue.matchingDataType(dataType).onFailure { return it }
            return Validated.ok()
        }

        fun validateOnlyMaxValue(maxValue: MaxValue, dataType: DynamicValue.DataType): Validated<Failure> {
            maxValue.checkDataType().onFailure { return it }
            maxValue.matchingDataType(dataType).onFailure { return it }
            return Validated.ok()
        }

        fun validateRange(minValue: MinValue, maxValue: MaxValue, dataType: DynamicValue.DataType): Validated<Failure> {
            fun DynamicValue.asBigDecimal(): BigDecimal = ((this as? DynamicValue.Number)?.underlying)
                ?: BigDecimal.valueOf((this as DynamicValue.Integer).underlying)

            maxValue.checkDataType().onFailure { return it }
            minValue.matchingDataType(dataType).onFailure { return it }

            maxValue.checkDataType().onFailure { return it }
            maxValue.matchingDataType(dataType).onFailure { return it }

            // VR.COM-17.1.36
            val min = minValue.value.asBigDecimal()
            val max = maxValue.value.asBigDecimal()
            return if (min < max)
                Validated.ok()
            else
                ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidRange().asValidatedError()
        }

        /**
         * VR.COM-17.1.20
         */
        fun ExpectedValue.checkDataType() = when (value) {
            is DynamicValue.Boolean,
            is DynamicValue.Integer,
            is DynamicValue.Number -> Validated.ok()

            is DynamicValue.String ->
                ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidTypeExpectedValue(
                    dataType = value.dataType
                ).asValidatedError()

        }

        /**
         * VR.COM-17.1.21
         */
        fun MinValue.checkDataType(): Validated<ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidTypeMinValue> =
            when (value) {
                is DynamicValue.Integer,
                is DynamicValue.Number -> Validated.ok()

                is DynamicValue.Boolean,
                is DynamicValue.String ->
                    ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidTypeMinValue(dataType = value.dataType)
                        .asValidatedError()
            }

        /**
         * VR.COM-17.1.32
         */
        fun MaxValue.checkDataType(): Validated<ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidTypeMaxValue> =
            when (value) {
                is DynamicValue.Integer,
                is DynamicValue.Number -> Validated.ok()

                is DynamicValue.Boolean,
                is DynamicValue.String ->
                    ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidTypeMaxValue(dataType = value.dataType)
                        .asValidatedError()
            }

        /**
         * VR.COM-17.1.33
         */
        fun ExpectedValue.matchingDataType(dataType: DynamicValue.DataType) =
            if (isDataTypeMatched(dataType))
                Validated.ok()
            else
                ValidatePCRErrors.Criterion.RequirementGroup.Requirement.ExpectedValueDataTypeMismatch()
                    .asValidatedError()

        /**
         * VR.COM-17.1.34
         */
        fun MinValue.matchingDataType(dataType: DynamicValue.DataType) =
            if (isDataTypeMatched(dataType))
                Validated.ok()
            else
                ValidatePCRErrors.Criterion.RequirementGroup.Requirement.MinValueDataTypeMismatch()
                    .asValidatedError()

        /**
         * VR.COM-17.1.35
         */
        fun MaxValue.matchingDataType(dataType: DynamicValue.DataType) =
            if (isDataTypeMatched(dataType))
                Validated.ok()
            else
                ValidatePCRErrors.Criterion.RequirementGroup.Requirement.MaxValueDataTypeMismatch()
                    .asValidatedError()

        /**
         * VR.COM-17.1.25
         */
        fun ValidatePCRDataCommand.Tender.Conversion.Coefficient.matchingDataType(
            path: String,
            dataType: DynamicValue.DataType
        ) = if (this.value.dataType == dataType)
            Validated.ok()
        else
            ValidatePCRErrors.Conversion.Coefficient.InvalidDataType(path = path).asValidatedError()

        /**
         * VR.COM-17.1.2
         */
        fun validateLotClassification(
            tenderClassification: ValidatePCRDataCommand.Classification,
            lotClassification: ValidatePCRDataCommand.Classification
        ): Validated<ValidatePCRErrors.Lot.InvalidClassificationId> =
            if (!lotClassification.equalsId(tenderClassification, 4))
                ValidatePCRErrors.Lot.InvalidClassificationId().asValidatedError()
            else
                Validated.ok()

        /**
         * VR.COM-17.1.5
         */
        fun validateItemClassification(
            tenderClassification: ValidatePCRDataCommand.Classification,
            itemClassification: ValidatePCRDataCommand.Classification
        ): Validated<ValidatePCRErrors.Item.InvalidClassificationId> =
            if (!itemClassification.equalsId(tenderClassification, 4))
                ValidatePCRErrors.Item.InvalidClassificationId().asValidatedError()
            else
                Validated.ok()

        fun ValidatePCRDataCommand.Classification.equalsId(
            other: ValidatePCRDataCommand.Classification,
            n: Int
        ): Boolean {
            if (scheme != other.scheme) return false
            if (id.length != other.id.length) return false
            return id.startsWith(prefix = other.id.substring(0, n), ignoreCase = true)
        }

        /**
         * VR.COM-17.1.16
         */
        fun validationRequirementGroupIds(
            criteria: List<ValidatePCRDataCommand.Tender.Criterion>
        ): Validated<ValidatePCRErrors.Criterion.RequirementGroup.DuplicateId> {
            val uniqueRequirementGroups = HashSet<String>()
            criteria.forEachIndexed { criterionIdx, criterion ->
                criterion.requirementGroups
                    .forEachIndexed { groupIdx, requirementGroup ->
                        if (!uniqueRequirementGroups.add(requirementGroup.id))
                            return ValidatePCRErrors.Criterion.RequirementGroup.DuplicateId(
                                path = "#/tender/criteria[$criterionIdx]/requirementGroups[$groupIdx]"
                            ).asValidatedError()

                    }
            }
            return Validated.ok()
        }

        /**
         * VR.COM-17.1.17
         */
        fun validationRequirementIds(
            criteria: List<ValidatePCRDataCommand.Tender.Criterion>
        ): Validated<ValidatePCRErrors.Criterion.RequirementGroup.Requirement.DuplicateId> {
            val uniqueRequirements = HashSet<String>()
            criteria.forEachIndexed { criterionIdx, criterion ->
                criterion.requirementGroups
                    .forEachIndexed { groupIdx, requirementGroup ->
                        requirementGroup.requirements
                            .forEachIndexed { requirementIdx, requirement ->
                                if (!uniqueRequirements.add(requirement.id))
                                    return ValidatePCRErrors.Criterion.RequirementGroup.Requirement.DuplicateId(
                                        path = "#/tender/criteria[$criterionIdx]/requirementGroups[$groupIdx]/requirements[$requirementIdx]"
                                    ).asValidatedError()
                            }
                    }
            }
            return Validated.ok()
        }

        fun isCriteriaNeed(awardCriteria: AwardCriteria): Boolean =
            when (awardCriteria) {
                AwardCriteria.COST_ONLY,
                AwardCriteria.QUALITY_ONLY,
                AwardCriteria.RATED_CRITERIA -> true

                AwardCriteria.PRICE_ONLY -> false
            }

        fun validateCriteriaExistence(criteria: List<ValidatePCRDataCommand.Tender.Criterion>): Validated<Failure> =
            if (criteria.isEmpty())
                ValidatePCRErrors.Criterion.MissingCriteria().asValidatedError()
            else
                Validated.ok()

        fun isConversionsNeed(awardCriteria: AwardCriteria): Boolean =
            when (awardCriteria) {
                AwardCriteria.COST_ONLY,
                AwardCriteria.QUALITY_ONLY,
                AwardCriteria.RATED_CRITERIA -> true

                AwardCriteria.PRICE_ONLY -> false
            }

        fun validateConversionsNotExists(conversions: List<ValidatePCRDataCommand.Tender.Conversion>): Validated<Failure> =
            if (conversions.isNotEmpty())
                ValidatePCRErrors.Conversion.RedundantConversionsList(path = "$/tender/conversions[*]")
                    .asValidatedError()
            else
                Validated.ok()

        fun ValidatePCRDataCommand.Tender.Criterion.isRelatesToLotOrItem(): Boolean =
            when (relatesTo) {
                CriterionRelatesTo.LOT,
                CriterionRelatesTo.ITEM -> true

                CriterionRelatesTo.AWARD,
                CriterionRelatesTo.TENDER,
                CriterionRelatesTo.TENDERER -> false
            }

        fun ValidatePCRDataCommand.Tender.Criterion.isRelatesToTender(): Boolean =
            when (relatesTo) {
                CriterionRelatesTo.TENDER -> true

                CriterionRelatesTo.AWARD,
                CriterionRelatesTo.ITEM,
                CriterionRelatesTo.LOT,
                CriterionRelatesTo.TENDERER -> false
            }

        fun ValidatePCRDataCommand.Tender.Criterion.isOther(): Boolean =
            this.classification.id.startsWith(CriterionCategory.OTHER.key, true)

        fun calculateSpecificWeightPrice(
            requirementsCombination: Combination<Requirements>,
            minCoefficientForRequirements: Map<String, BigDecimal>
        ): BigDecimal {

            val multiplyOnMinCoefficient: (BigDecimal, String) -> BigDecimal = { acc, requirementId ->
                acc * (minCoefficientForRequirements[requirementId] ?: BigDecimal.ONE)
            }

            return requirementsCombination.product
                .flatten()
                .fold(BigDecimal.ONE, multiplyOnMinCoefficient)
        }

        fun getMinCoefficients(conversions: List<ValidatePCRDataCommand.Tender.Conversion>): Map<String, BigDecimal> {
            return conversions.filter { it.relatesTo == ConversionRelatesTo.REQUIREMENT }
                .associate { conversion ->
                    val minCoefficient = conversion.coefficients
                        .minByOrNull { it.coefficient.rate }
                        ?.coefficient?.rate
                        ?: BigDecimal.ONE

                    conversion.relatedItem to minCoefficient
                }
        }

        fun getCriteriaPackageByLot(
            lots: List<String>,
            items: List<Pair<String, String>>,
            criteria: List<ValidatePCRDataCommand.Tender.Criterion>
        ): List<List<ValidatePCRDataCommand.Tender.Criterion>> {

            val tenderCriteria = criteria.filter { it.relatesTo == CriterionRelatesTo.TENDER }

            return lots.map { lotId ->
                val lotCriteria = criteria.filter { it.relatedItem == lotId }
                val itemsForLot = items.asSequence()
                    .filter { (_, relatedLot) -> relatedLot == lotId }
                    .map { (id, _) -> id }
                    .toSet()

                val itemsCriteriaForLot = criteria.filter { it.relatedItem in itemsForLot }

                (tenderCriteria + lotCriteria + itemsCriteriaForLot)
            }
        }
    }
}

object SpecificWeightedPrice {

    object Model {
        class CriteriaMatrix(values: List<Criterion>) : List<Criterion> by values
        class Criterion(values: List<RequirementGroup>) : List<RequirementGroup> by values
        data class RequirementGroup(val requirements: Requirements)

        data class Requirements(private val ids: List<String>) : List<String> by ids {

            override fun toString(): String {
                return ids.joinToString(prefix = "{", separator = "-", postfix = "}")
            }
        }
    }

    object Operations {

        data class Combination<T>(val product: Product<T>)
        class Product<T>(args: List<T>) : List<T> by args

        fun buildRequirementsMatrix(criteria: List<ValidatePCRDataCommand.Tender.Criterion>): CriteriaMatrix {

            fun toRequirementGroup(requirementGroup: ValidatePCRDataCommand.Tender.Criterion.RequirementGroup): RequirementGroup =
                requirementGroup.requirements
                    .map { it.id }
                    .let { RequirementGroup(Requirements(it)) }

            fun toCriterion(criterion: ValidatePCRDataCommand.Tender.Criterion): Criterion =
                criterion.requirementGroups
                    .map { toRequirementGroup(it) }
                    .let { Criterion(it) }

            return criteria
                .map { toCriterion(it) }
                .let { CriteriaMatrix(it) }
        }

        fun <T> product(arg1: Collection<Product<T>>, arg2: Collection<T>): Collection<Product<T>> {
            if (arg1.isEmpty()) return arg2.map { Product(listOf(it)) }
            if (arg2.isEmpty()) return arg1

            val productSet = mutableListOf<Product<T>>()
            arg1.forEach { x ->
                arg2.forEach { y ->
                    productSet.add(Product(x + listOf(y)))
                }
            }
            return productSet
        }

        fun getAllRequirementsCombinations(matrix: CriteriaMatrix): List<Combination<Requirements>> {

            val combinations = mutableListOf<Combination<Requirements>>()
            // Ex: for iteration 2: ({a1, b1},{a2, b2} ... {an, bn})

            for (rowIndex in 0 until matrix.size) {
                val row = matrix[rowIndex].map { it.requirements }

                val products = combinations.map { it.product }

                // ({a1, b1, c1},{a2, b2, c2} ...)
                val combinationsForIteration = product(products, row).map { Combination(it) }

                // remove combinations aren't already needed
                // ({a1, b1},{a2, b2} ... {an, bn}) -> ()
                combinations.clear()

                combinations.addAll(combinationsForIteration)
            }

            return combinations
        }
    }
}
