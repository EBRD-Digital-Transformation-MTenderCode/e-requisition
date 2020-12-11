package com.procurement.requisition.application.service.get.award

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsCommand
import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Service
import com.procurement.requisition.application.service.get.award.ToGetAwardCriteriaAndConversionsResultConverter as AwardCriteriaAndConversionsConvertor

@Service
class GetAwardCriteriaAndConversionsService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetAwardCriteriaAndConversionsCommand): Result<GetAwardCriteriaAndConversionsResult?, Failure> =
        pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .map { pcr ->
                pcr?.let { AwardCriteriaAndConversionsConvertor.fromDomain(pcr.tender) }
            }
}
