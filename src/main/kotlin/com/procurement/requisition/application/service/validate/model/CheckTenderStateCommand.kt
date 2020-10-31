package com.procurement.requisition.application.service.validate.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails

data class CheckTenderStateCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val pmd: ProcurementMethodDetails,
    val country: String,
    val operationType: OperationType
)
