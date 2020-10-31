package com.procurement.requisition.application.service.get.tender.owner

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.get.tender.owner.model.GetTenderOwnerCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetTenderOwnerService(
    val pcrRepository: PCRRepository,
    val pcrDeserializer: PCRDeserializer
) {

    fun get(command: GetTenderOwnerCommand): Result<String, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return GetTenderOwnerErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        return pcr.owner.asSuccess()
    }
}
