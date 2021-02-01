package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GetOcidFromRelatedProcessResponse(
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String
)

