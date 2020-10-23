package com.procurement.requisition.infrastructure.handler.pcr.query.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTenderStateRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String
)
