package com.procurement.requisition.application.service.set.tender

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.set.tender.model.SetTenderStatusSuspendedCommand
import com.procurement.requisition.application.service.set.tender.model.SetTenderStatusSuspendedResult
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderStatusSuspendedService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
) {

    fun set(command: SetTenderStatusSuspendedCommand): Result<SetTenderStatusSuspendedResult, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return SetTenderStatusSuspendedErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val updatedPcr = pcr.copy(tender = pcr.tender.copy(statusDetails = TenderStatusDetails.SUSPENDED))
        val json = pcrSerializer.build(updatedPcr).onFailure { return it }
        val state = TenderState(status = updatedPcr.tender.status, statusDetails = updatedPcr.tender.statusDetails)
        pcrRepository.update(
            cpid = command.cpid,
            ocid = command.ocid,
            state = state,
            data = json
        )

        return SetTenderStatusSuspendedResult(
            SetTenderStatusSuspendedResult.Tender(
                status = updatedPcr.tender.status,
                statusDetails = updatedPcr.tender.statusDetails
            )
        ).asSuccess()
    }
}
