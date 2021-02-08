package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.ProcurementMethodDetails

data class CheckTenderStateCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType
)
