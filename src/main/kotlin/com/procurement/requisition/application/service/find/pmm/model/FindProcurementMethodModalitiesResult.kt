package com.procurement.requisition.application.service.find.pmm.model

import com.procurement.requisition.domain.model.tender.ProcurementMethodModality

data class FindProcurementMethodModalitiesResult(
    val tender: Tender
) {
    data class Tender(
        val procurementMethodModalities: List<ProcurementMethodModality>
    )
}
