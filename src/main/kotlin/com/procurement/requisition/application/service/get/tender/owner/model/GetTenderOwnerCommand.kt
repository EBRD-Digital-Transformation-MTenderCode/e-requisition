package com.procurement.requisition.application.service.get.tender.owner.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetTenderOwnerCommand(
    val cpid: Cpid,
    val ocid: Ocid
)
