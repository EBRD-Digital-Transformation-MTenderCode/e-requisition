package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails

data class SetLotsStateResult(
    val tender: Tender
) {

    data class Tender(
        val lots: List<Lot>
    ) {

        data class Lot(
            val id: LotId,
            val status: LotStatus,
            val statusDetails: LotStatusDetails,
        )
    }
}
