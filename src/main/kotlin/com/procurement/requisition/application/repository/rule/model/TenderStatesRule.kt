package com.procurement.requisition.application.repository.rule.model

import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails

class TenderStatesRule(states: List<TenderState>) : List<TenderStatesRule.TenderState> by states {
    data class TenderState(
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails
    )
}

fun TenderStatesRule.contains(status: TenderStatus, statusDetails: TenderStatusDetails): Boolean {
    forEach { state ->
        if (state.status == status && state.statusDetails == statusDetails)
            return true
    }
    return false
}
