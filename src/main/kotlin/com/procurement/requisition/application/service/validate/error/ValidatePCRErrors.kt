package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.application.service.validate.SpecificWeightedPrice
import com.procurement.requisition.domain.extension.asString
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.lib.fail.Failure
import java.time.LocalDateTime

sealed class ValidatePCRErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    sealed class Lot(code: String, description: String) : ValidatePCRErrors(code = code, description = description) {
        class DuplicateId : Lot(code = "VR.COM-17.1.1", description = "")
        class InvalidClassificationId : Lot(code = "VR.COM-17.1.2", description = "")
        class VariantsDetails(lotId: String) :
            Lot(
                code = "VR.COM-17.1.3",
                description = "Invalid value of attribute 'variantsDetails' in lot with id '$lotId'."
            )

        class MissingItem : Lot(code = "VR.COM-17.1.29", description = "")
    }

    sealed class Item(code: String, description: String) : ValidatePCRErrors(code = code, description = description) {
        class DuplicateId : Item(code = "VR.COM-17.1.4", description = "")
        class InvalidClassificationId : Item(code = "VR.COM-17.1.5", description = "")
        class InvalidQuantity : Item(code = "VR.COM-17.1.6", description = "")
        class InvalidRelatedLot : Item(code = "VR.COM-17.1.7", description = "")
    }

    sealed class Target(code: String, description: String) : ValidatePCRErrors(code = code, description = description) {
        class DuplicateId : Target(code = "VR.COM-17.1.8", description = "")
        class InvalidRelatedItem : Target(code = "VR.COM-17.1.9", description = "")

        sealed class Observation(code: String, description: String) : Target(code = code, description = description) {
            class DuplicateId(path: String) :
                Observation(code = "VR.COM-17.1.10", description = "Duplicate id. Path: '$path'.")

            class InvalidPeriod(path: String, startDate: LocalDateTime, endDate: LocalDateTime) :
                Observation(
                    code = "VR.COM-17.1.11",
                    description = "Start-date '${startDate.asString()} equals or more than end-date '${endDate.asString()}'. Path: '$path'."
                )

            class InvalidRelatedRequirementId(path: String, relatedRequirementId: String) : Observation(
                code = "VR.COM-17.1.12",
                description = "Invalid related requirement id '$relatedRequirementId'. Path: '$path'."
            )
        }
    }

    sealed class Criterion(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId(path: String) :
            Criterion(code = "VR.COM-17.1.13", description = "Duplicate id. Path: '$path'.")

        class InvalidRelatedItem(path: String, relatedItem: String) :
            Criterion(code = "VR.COM-17.1.14", description = "Invalid related item '$relatedItem'. Path: '$path'.")

        class UnknownAttributeRelatedItem : Criterion(code = "VR.COM-17.1.15", description = "")

        class MissingRelatedItem(path: String) : Criterion(
            code = "VR.COM-17.1.31",
            description = "Missing required relatedItem. Path: '$path'."
        )

        class MissingCriteria : Criterion(code = "VR.COM-17.1.39", description = "Missing required criteria.")

        class TooSmallSpecificWeightPrice(
            combination: SpecificWeightedPrice.Operations.Combination<SpecificWeightedPrice.Model.Requirements>
        ) : Criterion(
            code = "VR.COM-17.1.37",
            description = "Too small specific weight price. Combination: ${combination.product.joinToString()}"
        )

        sealed class RequirementGroup(code: String, description: String) :
            Criterion(code = code, description = description) {

            class DuplicateId(path: String) :
                RequirementGroup(code = "VR.COM-17.1.16", description = "Duplicate id. Path: '$path'.")

            sealed class Requirement(code: String, description: String) :
                RequirementGroup(code = code, description = description) {

                class DuplicateId(path: String) :
                    RequirementGroup(code = "VR.COM-17.1.17", description = "Duplicate id. Path: '$path'.")

                class InvalidPeriod(path: String, startDate: LocalDateTime, endDate: LocalDateTime) :
                    RequirementGroup(
                        code = "VR.COM-17.1.18",
                        description = "Start-date '${startDate.asString()} equals or more than end-date '${endDate.asString()}'. Path: '$path'."
                    )

                class WrongValueAttributesCombination(id: String) :
                    RequirementGroup(code = "VR.COM-17.1.19", description = "Requirement id '$id'.")

                class InvalidTypeExpectedValue(dataType: DynamicValue.DataType) :
                    RequirementGroup(
                        code = "VR.COM-17.1.20",
                        description = "Expected value is invalid type '${dataType.key}'."
                    )

                class InvalidTypeMinValue(dataType: DynamicValue.DataType) :
                    RequirementGroup(
                        code = "VR.COM-17.1.21",
                        description = "Min value is invalid type '${dataType.key}'."
                    )

                class InvalidTypeMaxValue(dataType: DynamicValue.DataType) :
                    RequirementGroup(
                        code = "VR.COM-17.1.32",
                        description = "Max value is invalid type '${dataType.key}'."
                    )

                class ExpectedValueDataTypeMismatch :
                    RequirementGroup(
                        code = "VR.COM-17.1.33",
                        description = "The data type of the 'expectedValue' attribute does not match the value of the 'dataType' attribute"
                    )

                class MinValueDataTypeMismatch :
                    RequirementGroup(
                        code = "VR.COM-17.1.34",
                        description = "The data type of the 'minValue' attribute does not match the value of the 'dataType' attribute"
                    )

                class MaxValueDataTypeMismatch :
                    RequirementGroup(
                        code = "VR.COM-17.1.35",
                        description = "The data type of the 'maxValue' attribute does not match the value of the 'dataType' attribute"
                    )

                class InvalidRange : RequirementGroup(code = "VR.COM-17.1.36", description = "")
            }
        }
    }

    sealed class Conversion(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId(path: String) :
            Conversion(code = "VR.COM-17.1.22", description = "Duplicate id. Path: '$path'.")

        class InvalidRelatedItem(path: String, relatedItem: String) :
            Conversion(code = "VR.COM-17.1.23", description = "Invalid related item '$relatedItem'. Path: '$path'.")

        class RedundantConversionsList(path: String) :
            Conversion(code = "VR.COM-17.1.38", description = "Redundant conversions list. Path: $path")

        sealed class Coefficient(code: String, description: String) :
            Conversion(code = code, description = description) {

            class DuplicateId(path: String) :
                Conversion(code = "VR.COM-17.1.24", description = "Duplicate id. Path: '$path'.")

            class InvalidDataType(path: String) : Conversion(
                code = "VR.COM-17.1.25",
                description = "Invalid data-type. Path: '$path'."
            )

            class DuplicateValue(path: String) :
                Conversion(code = "VR.COM-17.1.30", description = "Duplicate value. Path: '$path'.")
        }
    }

    sealed class Document(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId(path: String) :
            Document(code = "VR.COM-17.1.26", description = "Duplicate id. Path: '$path'.")

        class InvalidRelatedLot(path: String, relatedLot: String) :
            Document(code = "VR.COM-17.1.27", description = "Invalid related lot '$relatedLot'. Path: '$path'.")
    }

    sealed class ProcurementMethodModality(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class MultiValue : ProcurementMethodModality(code = "VR.COM-17.1.28", description = "")
    }
}
