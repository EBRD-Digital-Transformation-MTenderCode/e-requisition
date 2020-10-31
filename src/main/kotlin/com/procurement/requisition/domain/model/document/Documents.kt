package com.procurement.requisition.domain.model.document

class Documents(values: List<Document> = emptyList()) : List<Document> by values {

    constructor(document: Document) : this(listOf(document))
}
