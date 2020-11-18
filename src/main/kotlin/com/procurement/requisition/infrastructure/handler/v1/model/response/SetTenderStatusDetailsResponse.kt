package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.infrastructure.handler.converter.asString

data class SetTenderStatusDetailsResponse(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
)

fun TenderState.convert() = SetTenderStatusDetailsResponse(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)
