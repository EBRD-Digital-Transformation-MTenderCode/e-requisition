package com.procurement.requisition.infrastructure.handler.pcr.query.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderStateResponse(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
)
