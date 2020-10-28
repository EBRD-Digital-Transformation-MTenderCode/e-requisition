package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.pmm.model

import com.procurement.requisition.application.service.find.pmm.model.FindProcurementMethodModalitiesResult

fun FindProcurementMethodModalitiesResult.convert(): FindProcurementMethodModalitiesResponse {
    val tender = FindProcurementMethodModalitiesResponse.Tender(
        procurementMethodModalities = tender.procurementMethodModalities.map { it.toString() }
    )

    return FindProcurementMethodModalitiesResponse(tender = tender)
}
