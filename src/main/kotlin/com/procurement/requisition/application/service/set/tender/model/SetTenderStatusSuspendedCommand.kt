package com.procurement.requisition.application.service.set.tender.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class SetTenderStatusSuspendedCommand(
    val cpid: Cpid,
    val ocid: Ocid,
)
