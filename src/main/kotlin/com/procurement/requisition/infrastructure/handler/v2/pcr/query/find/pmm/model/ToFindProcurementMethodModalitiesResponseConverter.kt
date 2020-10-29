package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.pmm.model

import com.procurement.requisition.application.service.find.pmm.model.FindProcurementMethodModalitiesResult
import com.procurement.requisition.infrastructure.handler.converter.asString

fun FindProcurementMethodModalitiesResult.convert(): FindProcurementMethodModalitiesResponse {
    val tender = FindProcurementMethodModalitiesResponse.Tender(
        procurementMethodModalities = tender.procurementMethodModalities.map { it.asString() }
    )

    return FindProcurementMethodModalitiesResponse(tender = tender)
}
