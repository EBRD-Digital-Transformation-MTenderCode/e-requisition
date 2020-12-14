package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.model.command.GetAwardCriteriaAndConversionsCommand
import com.procurement.requisition.application.service.model.result.GetAwardCriteriaAndConversionsResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Service
import com.procurement.requisition.application.service.converter.ToGetAwardCriteriaAndConversionsResultConverter as AwardCriteriaAndConversionsConvertor

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
