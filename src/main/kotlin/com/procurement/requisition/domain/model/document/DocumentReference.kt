package com.procurement.requisition.domain.model.document

import com.procurement.requisition.domain.model.EntityBase

data class DocumentReference(
    override val id: DocumentId,
) : EntityBase<DocumentId>()
