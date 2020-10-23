package com.procurement.requisition.infrastructure.handler.pcr.query.model

import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.infrastructure.handler.converter.asString

fun TenderState.convert() = GetTenderStateResponse(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)
