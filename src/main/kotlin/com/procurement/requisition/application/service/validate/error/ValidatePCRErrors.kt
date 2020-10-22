package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.lib.fail.Failure

sealed class ValidatePCRErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    sealed class Lot(code: String, description: String) : ValidatePCRErrors(code = code, description = description) {
        class DuplicateId : Lot(code = "VR.COM-17.1.1", description = "")
        class InvalidClassificationId : Lot(code = "VR.COM-17.1.2", description = "")
        class VariantsDetails : Lot(code = "VR.COM-17.1.3", description = "")
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
            class DuplicateId : Observation(code = "VR.COM-17.1.10", description = "")
            class InvalidPeriod : Observation(code = "VR.COM-17.1.11", description = "")
            class InvalidRelatedRequirementId : Observation(code = "VR.COM-17.1.12", description = "")
        }
    }

    sealed class Criterion(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId : Criterion(code = "VR.COM-17.1.13", description = "")
        class InvalidRelatedItem : Criterion(code = "VR.COM-17.1.14", description = "")
        class UnknownAttributeRelatedItem : Criterion(code = "VR.COM-17.1.15", description = "")

        sealed class RequirementGroup(code: String, description: String) :
            Criterion(code = code, description = description) {

            class DuplicateId : RequirementGroup(code = "VR.COM-17.1.16", description = "")

            sealed class Requirement(code: String, description: String) :
                RequirementGroup(code = code, description = description) {

                class DuplicateId : RequirementGroup(code = "VR.COM-17.1.17", description = "")
                class InvalidPeriod : RequirementGroup(code = "VR.COM-17.1.18", description = "")
                class UnknownAttributeRange : RequirementGroup(code = "VR.COM-17.1.19", description = "")
                class UnknownAttributeExpectedValue : RequirementGroup(code = "VR.COM-17.1.20", description = "")
                class InvalidRange : RequirementGroup(code = "VR.COM-17.1.21", description = "")
            }
        }
    }

    sealed class Conversion(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId : Conversion(code = "VR.COM-17.1.22", description = "")
        class InvalidRelatedItem : Conversion(code = "VR.COM-17.1.23", description = "")

        sealed class Coefficient(code: String, description: String) :
            Conversion(code = code, description = description) {

            class DuplicateId : Conversion(code = "VR.COM-17.1.24", description = "")
            class InvalidDataType : Conversion(code = "VR.COM-17.1.25", description = "")
        }
    }

    sealed class Document(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class DuplicateId : Document(code = "VR.COM-17.1.26", description = "")
        class InvalidRelatedLot : Document(code = "VR.COM-17.1.27", description = "")
    }

    sealed class ProcurementMethodModality(code: String, description: String) :
        ValidatePCRErrors(code = code, description = description) {

        class MultiValue : ProcurementMethodModality(code = "VR.COM-17.1.28", description = "")
    }
}
