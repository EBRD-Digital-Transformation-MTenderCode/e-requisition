package com.procurement.requisition.application.service.get.award.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetAwardCriteriaAndConversionsCommand(
    val cpid: Cpid,
    val ocid: Ocid
)
