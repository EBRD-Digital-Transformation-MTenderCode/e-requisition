package com.procurement.requisition.application.service.set.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import java.time.LocalDateTime

data class SetLotsStatusUnsuccessfulCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val startDate: LocalDateTime,
    val lots: List<Lot>
) {
    data class Lot(
        val id: LotId
    )
}