package com.procurement.requisition.application.service.create.request.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class CreateRequestsForEvPanelsCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: String
)
