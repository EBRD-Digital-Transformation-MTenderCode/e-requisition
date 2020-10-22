package com.procurement.requisition.domain.model.tender.criterion

class Criteria(values: List<Criterion> = emptyList()) : List<Criterion> by values {

    constructor(criterion: Criterion) : this(listOf(criterion))
}
