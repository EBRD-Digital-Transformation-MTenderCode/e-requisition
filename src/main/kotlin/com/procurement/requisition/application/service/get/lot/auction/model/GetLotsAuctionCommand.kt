package com.procurement.requisition.application.service.get.lot.auction.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetLotsAuctionCommand(
    val cpid: Cpid,
    val ocid: Ocid
)
