package com.procurement.requisition.application.service.set.tender.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class SetTenderUnsuspendedCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val phase: String
)
