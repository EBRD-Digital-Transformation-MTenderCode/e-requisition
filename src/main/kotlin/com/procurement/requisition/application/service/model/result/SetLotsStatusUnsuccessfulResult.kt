package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus

data class SetLotsStatusUnsuccessfulResult(
    val tender: Tender
) {
    data class Tender(
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails,
        val lots: List<Lot>
    ) {

        data class Lot(
            val id: LotId,
            val status: LotStatus
        )
    }
}
