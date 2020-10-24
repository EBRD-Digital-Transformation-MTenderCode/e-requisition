package com.procurement.requisition.application.service.get.tender.state

import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.get.tender.state.error.GetTenderStateErrors
import com.procurement.requisition.application.service.get.tender.state.model.GetTenderStateCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetTenderStateService(val pcrRepository: PCRRepository, val transform: Transform) {

    fun get(command: GetTenderStateCommand): Result<TenderState, Failure> {
        val tenderState = pcrRepository.getTenderState(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }

        return tenderState?.asSuccess()
            ?: GetTenderStateErrors.TenderNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()
    }
}
