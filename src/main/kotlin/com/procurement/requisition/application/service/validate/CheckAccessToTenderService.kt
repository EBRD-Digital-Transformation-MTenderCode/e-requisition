package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.validate.error.CheckAccessToTenderErrors
import com.procurement.requisition.application.service.validate.model.CheckAccessToTenderCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckAccessToTenderService(
    private val pcrManagement: PCRManagementService,
) {

    fun check(command: CheckAccessToTenderCommand): Validated<Failure> {
        val credential = pcrManagement.findCredential(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckAccessToTenderErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        if (command.token != credential.token)
            return CheckAccessToTenderErrors.InvalidToken.asValidatedError()

        if (command.owner != credential.owner)
            return CheckAccessToTenderErrors.InvalidOwner.asValidatedError()

        return Validated.ok()
    }
}
