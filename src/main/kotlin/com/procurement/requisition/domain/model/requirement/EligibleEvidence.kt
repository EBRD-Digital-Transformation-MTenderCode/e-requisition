package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.domain.model.document.DocumentReference

data class EligibleEvidence(
    val id: String,
    val title: String,
    val type: EligibleEvidenceType,
    val description: String?,
    val relatedDocument: DocumentReference?
)