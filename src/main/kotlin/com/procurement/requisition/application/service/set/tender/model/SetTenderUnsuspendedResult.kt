package com.procurement.requisition.application.service.set.tender.model

import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails

data class SetTenderUnsuspendedResult(
    val tender: Tender
) {
    data class Tender(
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails,
        val procurementMethodModalities: List<ProcurementMethodModality>,
    )
}
