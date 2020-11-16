package com.procurement.requisition.application.service.set.tender.model

import com.procurement.requisition.domain.model.tender.ProcurementMethodModality

data class SetTenderUnsuspendedResult(
    val tender: Tender
) {
    data class Tender(
        val status: String,
        val statusDetails: String,
        val procurementMethodModalities: List<ProcurementMethodModality>,
    )
}