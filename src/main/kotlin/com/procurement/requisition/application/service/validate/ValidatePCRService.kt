package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.service.validate.error.ValidatePCRErrors
import com.procurement.requisition.application.service.validate.model.ValidatePCRDataCommand
import com.procurement.requisition.domain.failure.incident.InvalidArgumentValueIncident
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.dataType
import com.procurement.requisition.domain.model.isNotUniqueIds
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.isDataTypeMatched
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality.REQUIRES_ELECTRONIC_CATALOGUE
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import com.procurement.requisition.lib.isUnique
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ValidatePCRService {

    fun validate(command: ValidatePCRDataCommand): Validated<Failure> {
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

        command.tender.criteria
            .forEachIndexed { criterionIdx, criterion ->

                //VR.COM-17.1.31
                if (criterion.relatesTo != null && criterion.relatedItem == null)
                    return ValidatePCRErrors.Criterion.MissingRelatedItem("#/tender/criteria[$criterionIdx]")
                        .asValidatedError()

                // VR.COM-17.1.15
                if (criterion.relatesTo == null && criterion.relatedItem != null)
                    return ValidatePCRErrors.Criterion.UnknownAttributeRelatedItem()
                        .asValidatedError()

                if (criterion.relatesTo != null && criterion.relatedItem != null) {
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

        // VR.COM-17.1.22
        if (command.tender.conversions.isNotUniqueIds())
            return ValidatePCRErrors.Conversion.DuplicateId(path = "#/tender/conversions").asValidatedError()

        val requirementIds = command.tender.criteria.asSequence()
            .flatMap { criterion -> criterion.requirementGroups.asSequence() }
            .flatMap { requirementGroup -> requirementGroup.requirements.asSequence() }
            .map { requirement -> requirement.id to requirement }
            .toMap()

        command.tender.conversions
            .forEachIndexed { conversionIdx, conversion ->
                // VR.COM-17.1.23
                if (conversion.relatedItem !in requirementIds)
                    return ValidatePCRErrors.Conversion.InvalidRelatedItem(
                        path = "#/tender/conversions[$conversionIdx]",
                        relatedItem = conversion.relatedItem
                    ).asValidatedError()

                // VR.COM-17.1.24
                if (conversion.coefficients.isNotUniqueIds())
                    return ValidatePCRErrors.Conversion.Coefficient.DuplicateId(
                        path = "#/tender/conversions[$conversionIdx]/coefficients"
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
    }
}
