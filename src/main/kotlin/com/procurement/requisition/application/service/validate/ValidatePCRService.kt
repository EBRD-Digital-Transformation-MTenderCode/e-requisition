package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.service.validate.error.ValidatePCRErrors
import com.procurement.requisition.application.service.validate.model.ValidatePCRData
import com.procurement.requisition.domain.model.isNotUniqueIds
import com.procurement.requisition.domain.model.requirement.RangeValue
import com.procurement.requisition.domain.model.requirement.RequirementDataType
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.lib.functional.ValidationResult
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ValidatePCRService {

    fun validate(data: ValidatePCRData): ValidationResult<ValidatePCRErrors> {
        // VR.COM-17.1.1
        if (data.tender.lots.isNotUniqueIds())
            return ValidationResult.error(ValidatePCRErrors.Lot.DuplicateId())

        data.tender.lots
            .forEach { lot ->
                // VR.COM-17.1.2
                if (lot.classification.equalsId(data.tender.classification, 4))
                    return ValidationResult.error(ValidatePCRErrors.Lot.InvalidClassificationId())

                // VR.COM-17.1.3
                if (!lot.variants.hasVariants && lot.variants.variantsDetails != null)
                    return ValidationResult.error(ValidatePCRErrors.Lot.VariantsDetails())
            }

        // VR.COM-17.1.4
        if (data.tender.items.isNotUniqueIds())
            return ValidationResult.error(ValidatePCRErrors.Item.DuplicateId())

        val lotIds = data.tender.lots.toSet { it.id }

        data.tender.items
            .forEach { item ->
                // VR.COM-17.1.5
                if (item.classification.equalsId(data.tender.classification, 4))
                    return ValidationResult.error(ValidatePCRErrors.Item.InvalidClassificationId())

                // VR.COM-17.1.6
                if (item.quantity <= BigDecimal.ZERO)
                    return ValidationResult.error(ValidatePCRErrors.Item.InvalidQuantity())

                // VR.COM-17.1.7
                if (item.relatedLot !in lotIds)
                    return ValidationResult.error(ValidatePCRErrors.Item.InvalidRelatedLot())
            }

        // VR.COM-17.1.29
        val itemsByRelatedLot = data.tender.items.toSet { it.relatedLot }
        data.tender.lots.forEach { lot ->
            if (lot.id !in itemsByRelatedLot)
                return ValidationResult.error(ValidatePCRErrors.Lot.MissingItem())
        }

        val itemsById = data.tender.items.associateBy { it.id }

        // VR.COM-17.1.8
        if (data.tender.targets.isNotUniqueIds())
            return ValidationResult.error(ValidatePCRErrors.Target.DuplicateId())
        data.tender.targets
            .forEach { target ->

                // VR.COM-17.1.9
                when (target.relatesTo) {
                    TargetRelatesTo.ITEM -> if (target.relatedItem !in itemsById)
                        return ValidationResult.error(ValidatePCRErrors.Target.InvalidRelatedItem())

                    TargetRelatesTo.LOT -> if (target.relatedItem !in lotIds)
                        return ValidationResult.error(ValidatePCRErrors.Target.InvalidRelatedItem())
                }

                // VR.COM-17.1.10
                if (target.observations.isNotUniqueIds())
                    return ValidationResult.error(ValidatePCRErrors.Target.Observation.DuplicateId())


                target.observations
                    .forEach { observation ->
                        // VR.COM-17.1.11
                        if (observation.period == null || observation.period.endDate.isAfter(observation.period.startDate))
                            Unit
                        else
                            return ValidationResult.error(ValidatePCRErrors.Target.Observation.InvalidPeriod())
                    }
            }

        if (data.tender.criteria.isNotUniqueIds())
            return ValidationResult.error(ValidatePCRErrors.Criterion.DuplicateId())

        data.tender.criteria
            .forEach { criterion ->
                // VR.COM-17.1.15
                if (criterion.relatesTo == null && criterion.relatedItem != null)
                    return ValidationResult.error(ValidatePCRErrors.Criterion.UnknownAttributeRelatedItem())

                if (criterion.relatesTo != null && criterion.relatedItem != null) {
                    // VR.COM-17.1.14
                    when (criterion.relatesTo) {
                        CriterionRelatesTo.ITEM -> if (criterion.relatedItem !in itemsById)
                            return ValidationResult.error(ValidatePCRErrors.Criterion.InvalidRelatedItem())

                        CriterionRelatesTo.LOT -> if (criterion.relatedItem !in lotIds)
                            return ValidationResult.error(ValidatePCRErrors.Criterion.InvalidRelatedItem())
                    }
                }

                // VR.COM-17.1.16
                if (criterion.requirementGroups.isNotUniqueIds())
                    return ValidationResult.error(ValidatePCRErrors.Criterion.RequirementGroup.DuplicateId())

                criterion.requirementGroups.forEach { requirementGroup ->
                    // VR.COM-17.1.17
                    if (requirementGroup.requirements.isNotUniqueIds())
                        return ValidationResult.error(ValidatePCRErrors.Criterion.RequirementGroup.DuplicateId())

                    requirementGroup.requirements.forEach { requirement ->
                        // VR.COM-17.1.18
                        if (requirement.period == null || requirement.period.endDate.isAfter(requirement.period.startDate))
                            Unit
                        else
                            return ValidationResult.error(ValidatePCRErrors.Criterion.RequirementGroup.Requirement.InvalidPeriod())

                        // VR.COM-17.1.19 - VR.COM-17.1.20 type-level validation

                        // VR.COM-17.1.21
                        if (requirement.value is RangeValue.AsInteger && requirement.value.minValue > requirement.value.maxValue)
                            return ValidationResult.error(ValidatePCRErrors.Criterion.RequirementGroup.Requirement.UnknownAttributeRange())
                        if (requirement.value is RangeValue.AsNumber && requirement.value.minValue > requirement.value.maxValue)
                            return ValidationResult.error(ValidatePCRErrors.Criterion.RequirementGroup.Requirement.UnknownAttributeRange())
                    }
                }
            }

        // VR.COM-17.1.22
        if (data.tender.conversions.isNotUniqueIds())
            return ValidationResult.error(ValidatePCRErrors.Conversion.DuplicateId())

        val requirementIds = data.tender.criteria.asSequence()
            .flatMap { criterion -> criterion.requirementGroups.asSequence() }
            .flatMap { requirementGroup -> requirementGroup.requirements.asSequence() }
            .map { requirement -> requirement.id to requirement }
            .toMap()

        data.tender.conversions
            .forEach { conversion ->
                // VR.COM-17.1.23
                if (conversion.relatedItem !in requirementIds)
                    return ValidationResult.error(ValidatePCRErrors.Conversion.InvalidRelatedItem())

                // VR.COM-17.1.24
                if (conversion.coefficients.isNotUniqueIds())
                    return ValidationResult.error(ValidatePCRErrors.Conversion.Coefficient.DuplicateId())

                val requirement = requirementIds.getValue(conversion.relatedItem)
                conversion.coefficients
                    .forEach { coefficient ->
                        // VR.COM-17.1.25
                        coefficient.value.validateDataType(requirement.dataType)
                    }
            }

        data.tender.targets
            .forEach { target ->
                target.observations
                    .forEach { observation ->
                        // VR.COM-17.1.12
                        if (observation.relatedRequirementId == null || observation.relatedRequirementId in requirementIds)
                            Unit
                        else
                            return ValidationResult.error(ValidatePCRErrors.Target.Observation.InvalidRelatedRequirementId())
                    }
            }

        // VR.COM-17.1.26
        if (data.tender.documents.isNotUniqueIds())
            return ValidationResult.error(ValidatePCRErrors.Document.DuplicateId())

        data.tender.documents
            .forEach { document ->
                // VR.COM-17.1.27
                if (document.relatedLots.isNotEmpty())
                    document.relatedLots.forEach { relatedLot ->
                        if (relatedLot !in lotIds)
                            return ValidationResult.error(ValidatePCRErrors.Document.InvalidRelatedLot())
                    }
            }

        // VR.COM-17.1.28
        if (data.tender.procurementMethodModalities.size > 1)
            return ValidationResult.error(ValidatePCRErrors.ProcurementMethodModality.MultiValue())

        return ValidationResult.ok()
    }

    fun CoefficientValue.validateDataType(requirementDataType: RequirementDataType):
        ValidationResult<ValidatePCRErrors.Conversion.Coefficient.InvalidDataType> {
        val isInvalidType = when (this) {
            is CoefficientValue.AsBoolean -> requirementDataType != RequirementDataType.BOOLEAN
            is CoefficientValue.AsString -> requirementDataType != RequirementDataType.STRING
            is CoefficientValue.AsNumber -> requirementDataType != RequirementDataType.NUMBER
            is CoefficientValue.AsInteger -> requirementDataType != RequirementDataType.INTEGER
        }
        return if (isInvalidType)
            ValidationResult.error(ValidatePCRErrors.Conversion.Coefficient.InvalidDataType())
        else
            ValidationResult.ok()
    }

    fun ValidatePCRData.Classification.equalsId(other: ValidatePCRData.Classification, n: Int): Boolean {
        if (scheme != other.scheme) return false
        if (id.length != other.id.length) return false
        return id.startsWith(prefix = other.id.substring(0, n), ignoreCase = true)
    }
}
