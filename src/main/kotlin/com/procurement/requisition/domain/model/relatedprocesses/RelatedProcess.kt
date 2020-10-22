package com.procurement.requisition.domain.model.relatedprocesses

data class RelatedProcess(
    val id: RelatedProcessId,
    val scheme: RelatedProcessScheme,
    val identifier: String,
    val relationship: Relationships,
    val uri: String
)
