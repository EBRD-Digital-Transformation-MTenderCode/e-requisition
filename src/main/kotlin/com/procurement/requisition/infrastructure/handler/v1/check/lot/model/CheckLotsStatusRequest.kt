package com.procurement.requisition.infrastructure.handler.v1.check.lot.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

class CheckLotsStatusRequest(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String
)
