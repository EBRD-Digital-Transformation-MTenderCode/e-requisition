package com.procurement.requisition.application.service.set

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.set.error.SetTenderStatusDetailsErrors
import com.procurement.requisition.application.service.set.model.SetTenderStatusDetailsCommand
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderStatusDetailsService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
) {

    fun set(command: SetTenderStatusDetailsCommand): Result<TenderState, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return SetTenderStatusDetailsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val tender = pcr.tender
        val updatedTender = tender.copy(
            statusDetails = tenderStatusDetails(command.phase).onFailure { return it }
        )
        val updatedPCR = pcr.copy(
            tender = updatedTender
        )

        val json = pcrSerializer.build(updatedPCR).onFailure { return it }
        val state = TenderState(status = updatedPCR.tender.status, statusDetails = updatedPCR.tender.statusDetails)
        pcrRepository.update(
            cpid = command.cpid,
            ocid = command.ocid,
            state = state,
            data = json
        ).onFailure { return it }

        return TenderState(
            status = updatedPCR.tender.status,
            statusDetails = updatedPCR.tender.statusDetails
        ).asSuccess()
    }

    fun tenderStatusDetails(phase: String): Result<TenderStatusDetails, Failure> = TenderStatusDetails.orNull(phase)
        ?.asSuccess()
        ?: Result.failure(SetTenderStatusDetailsErrors.CalculateTenderStatusDetails(phase = phase))
}
