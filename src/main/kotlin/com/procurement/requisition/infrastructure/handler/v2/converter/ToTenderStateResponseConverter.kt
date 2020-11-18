package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v2.model.response.GetTenderStateResponse

fun TenderState.convert() = GetTenderStateResponse(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)
