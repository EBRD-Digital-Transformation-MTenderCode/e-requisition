package com.procurement.requisition.application.service.validate.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token

data class CheckAccessToTenderCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: String
)
