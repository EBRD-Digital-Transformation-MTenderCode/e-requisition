package com.procurement.requisition.domain.model.requirement

class Requirements(values: List<Requirement> = emptyList()) : List<Requirement> by values {

    constructor(value: Requirement) : this(listOf(value))
}
