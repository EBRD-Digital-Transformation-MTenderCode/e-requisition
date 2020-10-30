package com.procurement.requisition.infrastructure.handler.v1.get.tender.owner

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderOwnerResponse(
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
)
