package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.domain.model.tender.lot.LotId

data class SetLotsStateCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType,
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
