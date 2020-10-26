package com.procurement.requisition.domain.model.tender.criterion

class Criteria(values: List<Criterion> = emptyList()) : List<Criterion> by values {

    constructor(criterion: Criterion) : this(listOf(criterion))

    operator fun plus(other: Criterion) = Criteria(this as List<Criterion> + other)
}
