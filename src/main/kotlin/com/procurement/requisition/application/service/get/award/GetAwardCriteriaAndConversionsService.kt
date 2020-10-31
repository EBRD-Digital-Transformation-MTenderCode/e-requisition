package com.procurement.requisition.application.service.get.award

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsCommand
import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service
import com.procurement.requisition.application.service.get.award.ToGetAwardCriteriaAndConversionsResultConverter as AwardCriteriaAndConversionsConvertor

@Service
class GetAwardCriteriaAndConversionsService(
    val pcrRepository: PCRRepository,
    val pcrDeserializer: PCRDeserializer
) {

    fun get(command: GetAwardCriteriaAndConversionsCommand): Result<GetAwardCriteriaAndConversionsResult?, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return null.asSuccess()

        return AwardCriteriaAndConversionsConvertor.fromDomain(pcr.tender).asSuccess()
    }
}
