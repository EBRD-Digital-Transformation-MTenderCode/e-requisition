package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.result.FindProcurementMethodModalitiesResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v2.model.response.FindProcurementMethodModalitiesResponse

fun FindProcurementMethodModalitiesResult.convert(): FindProcurementMethodModalitiesResponse {
    val tender = FindProcurementMethodModalitiesResponse.Tender(
        procurementMethodModalities = tender.procurementMethodModalities.map { it.asString() }
    )

    return FindProcurementMethodModalitiesResponse(tender = tender)
}
