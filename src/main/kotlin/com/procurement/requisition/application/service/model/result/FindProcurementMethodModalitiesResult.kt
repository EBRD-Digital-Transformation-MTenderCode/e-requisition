package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.tender.ProcurementMethodModality

data class FindProcurementMethodModalitiesResult(
    val tender: Tender
) {
    data class Tender(
        val procurementMethodModalities: List<ProcurementMethodModality>
    )
}
