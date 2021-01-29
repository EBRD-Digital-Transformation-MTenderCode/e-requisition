package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType

data class GetOcidFromRelatedProcessCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val operationType: OperationType
)