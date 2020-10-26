package com.procurement.requisition.application.service.set.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class SetTenderStatusDetailsCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val phase: String
)
