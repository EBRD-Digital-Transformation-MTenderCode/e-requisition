package com.procurement.requisition.domain.model.document

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.lot.RelatedLots

data class Document(
    override val id: DocumentId,
    val documentType: DocumentType,
    val title: String,
    val description: String?,
    val relatedLots: RelatedLots
) : EntityBase<DocumentId>()
