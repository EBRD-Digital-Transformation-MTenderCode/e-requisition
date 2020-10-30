package com.procurement.requisition.application.service.get.lot.auction.model

import com.procurement.requisition.domain.model.tender.lot.Lot

object ToGetLotsAuctionResultConverter {
    fun fromDomain(lot: Lot) = GetLotsAuctionResult.Tender.Lot(
        id = lot.id,
        title = lot.title,
        description = lot.description!!,
        value = lot.value!!
    )
}
