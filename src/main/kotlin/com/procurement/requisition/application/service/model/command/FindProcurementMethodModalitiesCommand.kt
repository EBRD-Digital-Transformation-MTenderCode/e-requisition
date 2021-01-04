package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality

data class FindProcurementMethodModalitiesCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender
) {
    data class Tender(
        val procurementMethodModalities: List<ProcurementMethodModality>
    )
}
