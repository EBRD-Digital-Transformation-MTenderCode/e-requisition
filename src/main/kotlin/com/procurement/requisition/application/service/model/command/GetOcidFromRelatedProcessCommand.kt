package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetOcidFromRelatedProcessCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val operationType: OperationType
)