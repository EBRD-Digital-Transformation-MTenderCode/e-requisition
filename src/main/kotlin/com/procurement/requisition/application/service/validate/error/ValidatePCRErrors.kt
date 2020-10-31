package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.domain.extension.format
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
                    description = "Start-date '${startDate.format()} equals or more than end-date '${endDate.format()}'. Path: '$path'."
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
                        description = "Start-date '${startDate.format()} equals or more than end-date '${endDate.format()}'. Path: '$path'."
                    )

                class UnknownAttributeRange : RequirementGroup(code = "VR.COM-17.1.19", description = "")
                class UnknownAttributeExpectedValue : RequirementGroup(code = "VR.COM-17.1.20", description = "")
                class InvalidRange : RequirementGroup(code = "VR.COM-17.1.21", description = "")
            }
        }
    }

    sealed class Conversion(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId(path: String) :
            Conversion(code = "VR.COM-17.1.22", description = "Duplicate id. Path: '$path'.")

        class InvalidRelatedItem(path: String, relatedItem: String) :
            Conversion(code = "VR.COM-17.1.23", description = "Invalid related item '$relatedItem'. Path: '$path'.")

        sealed class Coefficient(code: String, description: String) :
            Conversion(code = code, description = description) {

            class DuplicateId(path: String) :
                Conversion(code = "VR.COM-17.1.24", description = "Duplicate id. Path: '$path'.")

            class InvalidDataType(path: String) : Conversion(
                code = "VR.COM-17.1.25",
                description = "Invalid data-type. Path: '$path'."
            )
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
