package com.procurement.requisition.application.service.rule.model

import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails

class ValidLotStatesRule(private val items: List<State>) {

    data class State(
        val status: LotStatus,
        val statusDetails: LotStatusDetails?
    )

    fun contains(status: LotStatus, statusDetails: LotStatusDetails): Boolean =
        items.any { state ->
            if (state.statusDetails != null)
                status == state.status && statusDetails == state.statusDetails
            else
                state.status == status
        }
}
