package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import java.time.LocalDateTime

data class SetTenderStatusUnsuccessfulCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val startDate: LocalDateTime
)
