package com.procurement.requisition.infrastructure.handler.v1.get.lot.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.get.lot.model.ActiveLotIds

data class GetActiveLotIdsResponse(
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {

    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )
}

fun ActiveLotIds.convert() = GetActiveLotIdsResponse(
    lots = this.lots.map { lot -> lot.convert() }
)

fun ActiveLotIds.Lot.convert(): GetActiveLotIdsResponse.Lot = GetActiveLotIdsResponse.Lot(id = id.underlying)
