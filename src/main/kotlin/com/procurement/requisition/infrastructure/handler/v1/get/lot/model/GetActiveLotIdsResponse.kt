package com.procurement.requisition.infrastructure.handler.v1.get.lot.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.model.tender.lot.LotId

data class GetActiveLotIdsResponse(
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {

    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
    )
}

