package com.procurement.requisition.application.service.get.lot.auction.model

import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.Value
import com.procurement.requisition.domain.model.tender.lot.LotId

data class GetLotsAuctionResult(
    val tender: Tender
) {
    data class Tender(
        val id: TenderId,
        val title: String,
        val description: String,
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: LotId,
            val title: String,
            val description: String,
            val value: Value
        )
    }
}
