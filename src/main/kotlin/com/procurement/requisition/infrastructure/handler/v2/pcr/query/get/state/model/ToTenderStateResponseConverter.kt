package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.state.model

import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.infrastructure.handler.converter.asString

fun TenderState.convert() = GetTenderStateResponse(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)
