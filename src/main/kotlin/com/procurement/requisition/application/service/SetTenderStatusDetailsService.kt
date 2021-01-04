package com.procurement.requisition.application.service

import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.error.SetTenderStatusDetailsErrors
import com.procurement.requisition.application.service.model.command.SetTenderStatusDetailsCommand
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderStatusDetailsService(
    private val pcrManagement: PCRManagementService
) {

    fun set(command: SetTenderStatusDetailsCommand): Result<TenderState, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetTenderStatusDetailsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val tender = pcr.tender
        val updatedTender = tender.copy(
            statusDetails = tenderStatusDetails(command.phase).onFailure { return it }
        )
        val updatedPCR = pcr.copy(
            tender = updatedTender
        )

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPCR)
            .onFailure { return it }

        return TenderState(
            status = updatedPCR.tender.status,
            statusDetails = updatedPCR.tender.statusDetails
        ).asSuccess()
    }

    fun tenderStatusDetails(phase: String): Result<TenderStatusDetails, Failure> = TenderStatusDetails.orNull(phase)
        ?.asSuccess()
        ?: Result.failure(SetTenderStatusDetailsErrors.CalculateTenderStatusDetails(phase = phase))
}
