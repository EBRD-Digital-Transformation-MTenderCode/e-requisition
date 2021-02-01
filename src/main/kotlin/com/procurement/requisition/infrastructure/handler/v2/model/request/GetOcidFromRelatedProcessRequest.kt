package com.procurement.requisition.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class GetOcidFromRelatedProcessRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: String
)