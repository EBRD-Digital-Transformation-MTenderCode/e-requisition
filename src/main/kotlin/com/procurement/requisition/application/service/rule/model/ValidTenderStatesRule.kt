package com.procurement.requisition.application.service.rule.model

import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails

class ValidTenderStatesRule(private val items: List<State>) {

    data class State(
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails
    )

    fun contains(status: TenderStatus, statusDetails: TenderStatusDetails): Boolean =
        items.any { state ->
            status == state.status && statusDetails == state.statusDetails
        }
}
