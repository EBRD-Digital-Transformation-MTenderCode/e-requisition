package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.get.lot.auction.model.GetLotsAuctionResult
import com.procurement.requisition.infrastructure.handler.v1.model.response.GetLotsAuctionResponse

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
