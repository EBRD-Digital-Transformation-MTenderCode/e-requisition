package com.procurement.requisition.infrastructure.handler.v1.get.lot.model

import com.procurement.requisition.application.service.get.lot.model.ActiveLotIds

fun ActiveLotIds.convert() = GetActiveLotIdsResponse(
    lots = this.lots.map { lot -> lot.convert() }
)

fun ActiveLotIds.Lot.convert(): GetActiveLotIdsResponse.Lot = GetActiveLotIdsResponse.Lot(id = id)
