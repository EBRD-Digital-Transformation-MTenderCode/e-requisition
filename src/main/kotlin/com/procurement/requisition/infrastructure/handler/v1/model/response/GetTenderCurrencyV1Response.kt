package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderCurrencyV1Response(
    @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
) {
    data class Value(
        @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
    )
}
