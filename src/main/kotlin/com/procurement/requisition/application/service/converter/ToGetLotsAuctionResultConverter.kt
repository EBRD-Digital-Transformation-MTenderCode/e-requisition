package com.procurement.requisition.application.service.converter

import com.procurement.requisition.application.service.model.result.GetLotsAuctionResult
import com.procurement.requisition.domain.model.tender.lot.Lot

object ToGetLotsAuctionResultConverter {
    fun fromDomain(lot: Lot) = GetLotsAuctionResult.Tender.Lot(
        id = lot.id,
        title = lot.title,
        description = lot.description!!
    )
}
