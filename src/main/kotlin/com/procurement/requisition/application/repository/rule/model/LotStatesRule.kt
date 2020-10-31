package com.procurement.requisition.application.repository.rule.model

import com.procurement.requisition.domain.model.tender.lot.LotStatus

class LotStatesRule(val items: List<State>) {
    data class State(
        val status: LotStatus
    )
}

infix operator fun LotStatesRule.contains(status: LotStatus): Boolean {
    items.forEach { item ->
        if (item.status == status) return true
    }
    return false
}
