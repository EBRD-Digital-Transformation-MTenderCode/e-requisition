package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SetTenderStatusSuspendedResponse(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
)
