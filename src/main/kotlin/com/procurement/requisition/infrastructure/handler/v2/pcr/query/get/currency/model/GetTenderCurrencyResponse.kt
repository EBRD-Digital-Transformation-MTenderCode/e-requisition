package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.currency.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderCurrencyResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
    ) {
        data class Value(
            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
        )
    }
}
