package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId

data class GetItemsByLotIdsCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val lots: List<Lot>
) {
    data class Lot(
        val id: LotId
    )
}