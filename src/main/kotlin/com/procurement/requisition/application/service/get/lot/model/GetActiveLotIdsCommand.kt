package com.procurement.requisition.application.service.get.lot.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetActiveLotIdsCommand(
    val cpid: Cpid,
    val ocid: Ocid
)
