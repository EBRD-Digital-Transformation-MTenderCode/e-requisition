package com.procurement.requisition.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty

data class SetUnsuccessfulStateForLotsResponse(
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("status") @field:JsonProperty("status") val status: String,
            @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: String
        )
    }
}