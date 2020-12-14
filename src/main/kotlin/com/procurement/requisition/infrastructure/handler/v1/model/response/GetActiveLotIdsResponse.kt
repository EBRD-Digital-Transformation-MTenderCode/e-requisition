package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.model.result.GetActiveLotsResult

data class GetActiveLotIdsResponse(
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {

    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )
}

fun GetActiveLotsResult.convert() = GetActiveLotIdsResponse(
    lots = this.lots.map { lot -> lot.convert() }
)

fun GetActiveLotsResult.Lot.convert(): GetActiveLotIdsResponse.Lot = GetActiveLotIdsResponse.Lot(id = id.underlying)
