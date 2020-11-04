package com.procurement.requisition.infrastructure.handler.v1.set.tender.model

import com.procurement.requisition.application.service.set.tender.model.SetTenderUnsuspendedResult

fun SetTenderUnsuspendedResult.convert() = SetTenderUnsuspendedResponse(
    tender = SetTenderUnsuspendedResponse.Tender(
        status = tender.status,
        statusDetails = tender.statusDetails,
        procurementMethodModalities = tender.procurementMethodModalities
    )
)