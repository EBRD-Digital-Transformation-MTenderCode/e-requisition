package com.procurement.requisition.infrastructure.handler.v1.set.tender.model

import com.procurement.requisition.application.service.set.tender.model.SetTenderStatusSuspendedResult

fun SetTenderStatusSuspendedResult.convert() = SetTenderStatusSuspendedResponse(
    status = tender.status.key,
    statusDetails = tender.statusDetails.key
)
