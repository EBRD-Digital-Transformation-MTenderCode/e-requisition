package com.procurement.requisition.infrastructure.handler.v1.set.tender.model

import com.procurement.requisition.domain.model.tender.ProcurementMethodModality

data class SetTenderUnsuspendedResponse(
    val tender: Tender
) {
    data class Tender(
        val status: String,
        val statusDetails: String,
        val procurementMethodModalities: List<ProcurementMethodModality>,
    )
}