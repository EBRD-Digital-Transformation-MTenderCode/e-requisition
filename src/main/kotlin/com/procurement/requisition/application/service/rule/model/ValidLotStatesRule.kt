package com.procurement.requisition.application.service.rule.model

import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails

class ValidLotStatesRule(private val items: List<State>) {

    data class State(
        val status: Status,
        val statusDetails: StatusDetails?
    ) {

        data class Status(
            val value: LotStatus,
        )

        data class StatusDetails(
            val value: LotStatusDetails?,
        )
    }

    fun contains(status: LotStatus, statusDetails: LotStatusDetails?): Boolean =
        items.any { state ->
            if (state.statusDetails != null)
                status == state.status.value && statusDetails == state.statusDetails.value
            else
                state.status.value == status
        }
}
