package com.procurement.requisition.application.service.relation.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType

data class CreateRelationCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val relatedOcid: Ocid,
    val operationType: OperationType
)
