package com.procurement.requisition.application.service.get.tender.owner

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.get.tender.owner.model.GetTenderOwnerCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetTenderOwnerService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetTenderOwnerCommand): Result<String, Failure> = pcrManagement
        .findCredential(cpid = command.cpid, ocid = command.ocid)
        .onFailure { return it }
        ?.owner
        ?.asSuccess()
        ?: GetTenderOwnerErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()
}
