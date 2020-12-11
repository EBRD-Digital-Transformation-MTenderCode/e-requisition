package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class SetLotsStateResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
    ) {

        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
            @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
        )
    }
}
