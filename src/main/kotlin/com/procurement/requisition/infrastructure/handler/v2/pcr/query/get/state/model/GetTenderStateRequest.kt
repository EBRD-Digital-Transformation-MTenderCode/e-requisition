package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.state.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderStateRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String
)
