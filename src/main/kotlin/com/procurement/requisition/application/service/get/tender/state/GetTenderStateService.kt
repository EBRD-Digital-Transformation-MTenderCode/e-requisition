package com.procurement.requisition.application.service.get.tender.state

import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.get.tender.state.error.GetTenderStateErrors
import com.procurement.requisition.application.service.get.tender.state.model.GetTenderStateCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetTenderStateService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetTenderStateCommand): Result<TenderState, Failure> = pcrManagement
        .findState(cpid = command.cpid, ocid = command.ocid)
        .onFailure { return it }
        ?.asSuccess()
        ?: GetTenderStateErrors.TenderNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()
}
