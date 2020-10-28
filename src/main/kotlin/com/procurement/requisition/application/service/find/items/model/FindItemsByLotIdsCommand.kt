package com.procurement.requisition.application.service.find.items.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId

data class FindItemsByLotIdsCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender
) {
    data class Tender(
        val lots: List<LotId>
    )
}
