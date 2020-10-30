package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.currency.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderCurrencyRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String
)
