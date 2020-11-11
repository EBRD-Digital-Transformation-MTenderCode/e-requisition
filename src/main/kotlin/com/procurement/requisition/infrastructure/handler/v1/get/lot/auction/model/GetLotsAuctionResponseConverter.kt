package com.procurement.requisition.infrastructure.handler.v1.get.lot.auction.model

import com.procurement.requisition.application.service.get.lot.auction.model.GetLotsAuctionResult

fun GetLotsAuctionResult.convert() = GetLotsAuctionResponse(
    tender = GetLotsAuctionResponse.Tender(
        id = this.tender.id.underlying,
        title = this.tender.title,
        description = this.tender.description,
        lots = this.tender.lots.map { lot ->
            GetLotsAuctionResponse.Tender.Lot(
                id = lot.id.underlying,
                title = lot.title,
                description = lot.description
            )
        }
    )
)
