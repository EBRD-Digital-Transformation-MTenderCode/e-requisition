package com.procurement.requisition.domain.model.document

import com.procurement.requisition.domain.model.tender.lot.LotId

data class Document(
    val id: DocumentId,
    val documentType: DocumentType,
    val title: String,
    val description: String?,
    val relatedLots: List<LotId>
)
