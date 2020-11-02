package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.service.validate.error.ValidatePCRErrors
import com.procurement.requisition.application.service.validate.model.ValidatePCRDataCommand
import com.procurement.requisition.domain.failure.incident.InvalidArgumentValueIncident
import com.procurement.requisition.domain.model.isNotUniqueIds
import com.procurement.requisition.domain.model.requirement.RangeValue
import com.procurement.requisition.domain.model.requirement.RequirementDataType
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality.REQUIRES_ELECTRONIC_CATALOGUE
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import com.procurement.requisition.lib.isUnique
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ValidatePCRService {

    fun validate(command: ValidatePCRDataCommand): Validated<ValidatePCRErrors> {
        // VR.COM-17.1.1
        if (command.tender.lots.isNotUniqueIds())
            return Validated.error(ValidatePCRErrors.Lot.DuplicateId())

        command.tender.lots
            .forEach { lot ->
                // VR.COM-17.1.2
                validateLotClassification(
                    tenderClassification = command.tender.classification,
                    lotClassification = lot.classification
                ).onFailure { return it }

                if (!lot.classification.equalsId(command.tender.classification, 4))
                    return Validated.error(ValidatePCRErrors.Lot.InvalidClassificationId())

                // VR.COM-17.1.3
                lot.variants
                    .forEach { variant ->
                        if (!variant.hasVariants && variant.variantsDetails != null)
                            return Validated.error(ValidatePCRErrors.Lot.VariantsDetails(lot.id))
                    }

            }

        // VR.COM-17.1.4
        if (command.tender.items.isNotUniqueIds())
            return Validated.error(ValidatePCRErrors.Item.DuplicateId())

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
                    return Validated.error(ValidatePCRErrors.Item.InvalidQuantity())

                // VR.COM-17.1.7
                if (item.relatedLot !in lotIds)
                    return Validated.error(ValidatePCRErrors.Item.InvalidRelatedLot())
            }

        // VR.COM-17.1.29
        val itemsByRelatedLot = command.tender.items.toSet { it.relatedLot }
        if (REQUIRES_ELECTRONIC_CATALOGUE in command.tender.procurementMethodModalities) {
            command.tender.lots.forEach { lot ->
                if (lot.id !in itemsByRelatedLot)
                    return Validated.error(ValidatePCRErrors.Lot.MissingItem())
            }
        }

        val itemsById = command.tender.items.associateBy { it.id }

        // VR.COM-17.1.8
        if (command.tender.targets.isNotUniqueIds())
            return Validated.error(ValidatePCRErrors.Target.DuplicateId())
        command.tender.targets
            .forEach { target ->

                // VR.COM-17.1.9
                when (target.relatesTo) {
                    TargetRelatesTo.ITEM -> if (target.relatedItem !in itemsById)
                        return Validated.error(ValidatePCRErrors.Target.InvalidRelatedItem())

                    TargetRelatesTo.LOT -> if (target.relatedItem !in lotIds)
                        return Validated.error(ValidatePCRErrors.Target.InvalidRelatedItem())
                }

                // VR.COM-17.1.10
                if (target.observations.isNotUniqueIds())
                    return Validated.error(ValidatePCRErrors.Target.Observation.DuplicateId(path = "#/tender/targets[id=${target.id}]/observations"))


                target.observations
                    .forEach { observation ->
                        // VR.COM-17.1.11
                        observation.period
                            ?.apply {
                                if (startDate != null && endDate != null) {
                                    if (!startDate.isBefore(endDate))
                                        return Validated.error(
                                            ValidatePCRErrors.Target.Observation.InvalidPeriod(
                                                path = "#/tender/targets[id=${target.id}]/observations[id=${observation.id}]",
                                                startDate = startDate,
                                                endDate = endDate
                                            )
                                        )
                                }
                            }
                    }
            }

        if (command.tender.criteria.isNotUniqueIds())
            return Validated.error(ValidatePCRErrors.Criterion.DuplicateId(path = "#/tender/criteria"))

        // VR.COM-17.1.16
        validationRequirementGroupIds(command.tender.criteria).onFailure { return it }

        // VR.COM-17.1.17
        validationRequirementIds(command.tender.criteria).onFailure { return it }

        command.tender.criteria
            .forEach { criterion ->

                //VR.COM-17.1.31
                if (criterion.relatesTo != null && criterion.relatedItem == null)
                    return Validated.error(ValidatePCRErrors.Criterion.MissingRelatedItem("#/tender/criteria[id=${criterion.id}]"))

                // VR.COM-17.1.15
                if (criterion.relatesTo == null && criterion.relatedItem != null)
                    return Validated.error(ValidatePCRErrors.Criterion.UnknownAttributeRelatedItem())

                if (criterion.relatesTo != null && criterion.relatedItem != null) {
                    // VR.COM-17.1.14
                    when (criterion.relatesTo) {
                        CriterionRelatesTo.ITEM -> if (criterion.relatedItem !in itemsById)
                            return Validated.error(
                                ValidatePCRErrors.Criterion.InvalidRelatedItem(
                                    path = "#/tender/criteria[id=${criterion.id}]/relatesTo",
                                    relatedItem = criterion.relatedItem
                                )
                            )

                        CriterionRelatesTo.LOT -> if (criterion.relatedItem !in lotIds)
                            return Validated.error(
                                ValidatePCRErrors.Criterion.InvalidRelatedItem(
                                    path = "#/tender/criteria[id=${criterion.id}]/relatesTo",
                                    relatedItem = criterion.relatedItem
                                )
                            )

                        CriterionRelatesTo.TENDER,
                        CriterionRelatesTo.TENDERER,
                        CriterionRelatesTo.AWARD -> InvalidArgumentValueIncident(
                            name = "relatesTo",
                            value = criterion.relatesTo,
                            expectedValue = listOf(CriterionRelatesTo.ITEM, CriterionRelatesTo.LOT)
                        ).asValidatedError()
                    }
                }

                criterion.requirementGroups.forEach { requirementGroup ->
                    requirementGroup.requirements
                        .forEach { requirement ->
                            // VR.COM-17.1.18
                            requirement.period
                                ?.apply {
                                    if (startDate.isEqual(endDate) || startDate.isAfter(endDate))
                                        return Validated.error(
                                            ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidPeriod(
                                                path = "#/tender/requirementGroups[id=${requirementGroup.id}]/requirements[id=${requirement.id}]",
                                                startDate = startDate,
                                                endDate = endDate
                                            )
                                        )

                                }

                            // VR.COM-17.1.19 - VR.COM-17.1.20 type-level validation

                            // VR.COM-17.1.21
                            if (requirement.value is RangeValue.AsInteger && requirement.value.minValue >= requirement.value.maxValue)
                                return Validated.error(ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidRange())
                            if (requirement.value is RangeValue.AsNumber && requirement.value.minValue >= requirement.value.maxValue)
                                return Validated.error(ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidRange())
                        }
                }
            }

        // VR.COM-17.1.22
        if (command.tender.conversions.isNotUniqueIds())
            return Validated.error(ValidatePCRErrors.Conversion.DuplicateId(path = "#/tender/conversions"))

        val requirementIds = command.tender.criteria.asSequence()
            .flatMap { criterion -> criterion.requirementGroups.asSequence() }
            .flatMap { requirementGroup -> requirementGroup.requirements.asSequence() }
            .map { requirement -> requirement.id to requirement }
            .toMap()

        command.tender.conversions
            .forEach { conversion ->
                // VR.COM-17.1.23
                if (conversion.relatedItem !in requirementIds)
                    return Validated.error(
                        ValidatePCRErrors.Conversion.InvalidRelatedItem(
                            path = "#/tender/conversions[id={${conversion.id}}]",
                            relatedItem = conversion.relatedItem
                        )
                    )

                // VR.COM-17.1.24
                if (conversion.coefficients.isNotUniqueIds())
                    return Validated.error(
                        ValidatePCRErrors.Conversion.Coefficient.DuplicateId(
                            path = "#/tender/conversions[id={${conversion.id}}]/coefficients"
                        )
                    )

                //VR.COM-17.1.30
                if (!conversion.coefficients.isUnique { it.value })
                    return Validated.error(
                        ValidatePCRErrors.Conversion.Coefficient.DuplicateValue(
                            path = "#/tender/conversions[id={${conversion.id}}]/coefficients"
                        )
                    )

                val requirement = requirementIds.getValue(conversion.relatedItem)
                conversion.coefficients
                    .forEach { coefficient ->
                        // VR.COM-17.1.25
                        coefficient.value.validateDataType(
                            path = "#/tender/conversions[id={${conversion.id}}]/coefficients[id=${coefficient.id}]",
                            requirement.dataType
                        ).onFailure { return it }
                    }
            }

        command.tender.targets
            .forEach { target ->
                target.observations
                    .forEach { observation ->
                        // VR.COM-17.1.12
                        if (observation.relatedRequirementId == null || observation.relatedRequirementId in requirementIds)
                            Unit
                        else
                            return Validated.error(
                                ValidatePCRErrors.Target.Observation.InvalidRelatedRequirementId(
                                    path = "#/tender/targets[id={${target.id}}]/observations[id=${observation.id}]",
                                    relatedRequirementId = observation.relatedRequirementId
                                )
                            )
                    }
            }

        // VR.COM-17.1.26
        if (command.tender.documents.isNotUniqueIds())
            return Validated.error(ValidatePCRErrors.Document.DuplicateId(path = "#/tender/documents"))

        command.tender.documents
            .forEach { document ->
                // VR.COM-17.1.27
                if (document.relatedLots.isNotEmpty())
                    document.relatedLots.forEach { relatedLot ->
                        if (relatedLot !in lotIds)
                            return Validated.error(
                                ValidatePCRErrors.Document.InvalidRelatedLot(
                                    path = "#/tender/documents[id=${document.id}]",
                                    relatedLot = relatedLot
                                )
                            )
                    }
            }

        // VR.COM-17.1.28
        if (command.tender.procurementMethodModalities.size > 1)
            return Validated.error(ValidatePCRErrors.ProcurementMethodModality.MultiValue())

        return Validated.ok()
    }

    fun CoefficientValue.validateDataType(path: String, requirementDataType: RequirementDataType):
        Validated<ValidatePCRErrors.Conversion.Coefficient.InvalidDataType> {
        val isInvalidType = when (this) {
            is CoefficientValue.AsBoolean -> requirementDataType != RequirementDataType.BOOLEAN
            is CoefficientValue.AsString -> requirementDataType != RequirementDataType.STRING
            is CoefficientValue.AsNumber -> requirementDataType != RequirementDataType.NUMBER
            is CoefficientValue.AsInteger -> requirementDataType != RequirementDataType.INTEGER
        }
        return if (isInvalidType)
            Validated.error(ValidatePCRErrors.Conversion.Coefficient.InvalidDataType(path = path))
        else
            Validated.ok()
    }
}

/**
 * VR.COM-17.1.2
 */
fun validateLotClassification(
    tenderClassification: ValidatePCRDataCommand.Classification,
    lotClassification: ValidatePCRDataCommand.Classification
): Validated<ValidatePCRErrors.Lot.InvalidClassificationId> =
    if (!lotClassification.equalsId(tenderClassification, 4))
        Validated.error(ValidatePCRErrors.Lot.InvalidClassificationId())
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
        Validated.error(ValidatePCRErrors.Item.InvalidClassificationId())
    else
        Validated.ok()

fun ValidatePCRDataCommand.Classification.equalsId(other: ValidatePCRDataCommand.Classification, n: Int): Boolean {
    if (scheme != other.scheme) return false
    if (id.length != other.id.length) return false
    return id.startsWith(prefix = other.id.substring(0, n), ignoreCase = true)
}

/**
 * VR.COM-17.1.16
 */
fun validationRequirementGroupIds(criteria: List<ValidatePCRDataCommand.Tender.Criterion>):
    Validated<ValidatePCRErrors.Criterion.RequirementGroup.DuplicateId> {

    val uniqueRequirementGroups = HashSet<String>()
    criteria.forEachIndexed { criterionIdx, criterion ->
        criterion.requirementGroups
            .forEachIndexed { groupIdx, requirementGroup ->
                if (!uniqueRequirementGroups.add(requirementGroup.id))
                    return Validated.error(
                        ValidatePCRErrors.Criterion.RequirementGroup.DuplicateId(
                            path = "#/tender/criteria[$criterionIdx]/requirementGroups[$groupIdx]"
                        )
                    )
            }
    }

    return Validated.ok()
}

/**
 * VR.COM-17.1.17
 */
fun validationRequirementIds(criteria: List<ValidatePCRDataCommand.Tender.Criterion>):
    Validated<ValidatePCRErrors.Criterion.RequirementGroup.Requirement.DuplicateId> {

    val uniqueRequirements = HashSet<String>()
    criteria.forEachIndexed { criterionIdx, criterion ->
        criterion.requirementGroups
            .forEachIndexed { groupIdx, requirementGroup ->
                requirementGroup.requirements
                    .forEachIndexed { requirementIdx, requirement ->
                        if (!uniqueRequirements.add(requirement.id))
                            return Validated.error(
                                ValidatePCRErrors.Criterion.RequirementGroup.Requirement.DuplicateId(
                                    path = "#/tender/criteria[$criterionIdx]/requirementGroups[$groupIdx]/requirements[$requirementIdx]"
                                )
                            )
                    }
            }
    }

    return Validated.ok()
}
