package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.model.result.SetTenderStatusSuspendedResult
import com.procurement.requisition.infrastructure.handler.v1.model.response.SetTenderStatusSuspendedResponse

fun SetTenderStatusSuspendedResult.convert() = SetTenderStatusSuspendedResponse(
    status = tender.status.key,
    statusDetails = tender.statusDetails.key
)
