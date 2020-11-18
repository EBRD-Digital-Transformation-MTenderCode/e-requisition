package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.set.tender.model.SetTenderUnsuspendedResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v1.model.response.SetTenderUnsuspendedResponse

fun SetTenderUnsuspendedResult.convert() = SetTenderUnsuspendedResponse(
    tender = SetTenderUnsuspendedResponse.Tender(
        status = tender.status.asString(),
        statusDetails = tender.statusDetails.asString(),
        procurementMethodModalities = tender.procurementMethodModalities.map { it.asString() }
    )
)
