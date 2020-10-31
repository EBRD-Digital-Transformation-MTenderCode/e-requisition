package com.procurement.requisition.application.repository.rule.model

import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails

class TenderStatesRule(val items: List<State>) {
    data class State(
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails
    )
}

infix operator fun TenderStatesRule.contains(state: TenderStatesRule.State): Boolean {
    items.forEach { item ->
        if (item.status == state.status && item.statusDetails == state.statusDetails)
            return true
    }
    return false
}
