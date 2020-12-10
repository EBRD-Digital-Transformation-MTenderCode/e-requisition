package com.procurement.requisition.application.service.set.tender

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.set.tender.model.SetTenderUnsuspendedCommand
import com.procurement.requisition.application.service.set.tender.model.SetTenderUnsuspendedResult
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderUnsuspendedService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
) {

    fun set(command: SetTenderUnsuspendedCommand): Result<SetTenderUnsuspendedResult, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return SetTenderUnsuspendedErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        if (pcr.tender.statusDetails != TenderStatusDetails.SUSPENDED)
            return SetTenderUnsuspendedErrors.TenderNotUnsuspendedError().asFailure()

        val statusDetails = TenderStatusDetails.orNull(command.phase)
            ?: return SetTenderUnsuspendedErrors.TenderStatusDetailsParseFailed(command.phase).asFailure()
        val updatedPcr = pcr.copy(tender = pcr.tender.copy(statusDetails = statusDetails))
        val json = pcrSerializer.build(updatedPcr).onFailure { return it }
        val state = TenderState(status = pcr.tender.status, statusDetails = pcr.tender.statusDetails)
        pcrRepository.update(
            cpid = command.cpid,
            ocid = command.ocid,
            state = state,
            data = json
        )

        return SetTenderUnsuspendedResult(
            SetTenderUnsuspendedResult.Tender(
                updatedPcr.tender.status,
                updatedPcr.tender.statusDetails,
                updatedPcr.tender.procurementMethodModalities,
            )
        ).asSuccess()
    }
}
