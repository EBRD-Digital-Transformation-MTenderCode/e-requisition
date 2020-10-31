package com.procurement.requisition.domain.model.tender.target

class Targets(values: List<Target> = emptyList()) : List<Target> by values {

    constructor(target: Target) : this(listOf(target))
}
