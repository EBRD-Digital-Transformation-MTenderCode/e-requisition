package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.lib.fail.Failure

sealed class ValidateRequirementResponsesErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        ValidateRequirementResponsesErrors(
            code = "VR.COM-17.9.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    sealed class RequirementResponse(code: String, description: String) :
        ValidateRequirementResponsesErrors(code = code, description = description) {

        object DuplicateId :
            RequirementResponse(code = "VR.COM-17.9.2", description = "")

        sealed class Requirement(code: String, description: String) :
            RequirementResponse(code = code, description = description) {

            object DuplicateId :
                RequirementResponse(code = "VR.COM-17.9.7", description = "")

            class InvalidDataType(path: String) :
                RequirementResponse(code = "VR.COM-17.9.3", description = "Invalid data-type. Path: '$path'.")
        }

        class EmptyCriterion(criterionId: CriterionId) : RequirementResponse(
            code = "VR.COM-17.9.4",
            description = "Criterion '$criterionId'."
        )

        class MultiGroup(criterionId: CriterionId) : RequirementResponse(
            code = "VR.COM-17.9.5",
            description = "Criterion '$criterionId'."
        )

        class Unknown(ids: List<RequirementId>) :
            RequirementResponse(code = "VR.COM-17.9.6", description = "Unknown requirement with ids '$ids'.")

        class InvalidPeriod :
            RequirementResponse(code = "VR.COM-17.9.8", description = "")
    }
}
