package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails

data class SetTenderStatusSuspendedResult(
    val tender: Tender
) {
    data class Tender(
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails
    )
}
