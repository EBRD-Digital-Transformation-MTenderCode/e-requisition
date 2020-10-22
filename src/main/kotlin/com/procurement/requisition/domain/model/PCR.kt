package com.procurement.requisition.domain.model

import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcesses
import com.procurement.requisition.domain.model.tender.Tender

data class PCR(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: String,
    val tender: Tender,
    val relatedProcesses: RelatedProcesses
)
