package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId

data class SetUnsuccessfulStateForLotsCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender
) {
    data class Tender(
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: LotId
        )
    }
}