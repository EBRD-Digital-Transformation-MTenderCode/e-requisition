package com.procurement.requisition.domain.model.relatedprocesses

class Relationships(values: List<Relationship> = emptyList()) : List<Relationship> by values {

    constructor(relationship: Relationship) : this(listOf(relationship))
}
