package com.procurement.requisition.domain.model.tender.conversion.coefficient

class Coefficients(values: List<Coefficient> = emptyList()) : List<Coefficient> by values {

    constructor(coefficient: Coefficient) : this(listOf(coefficient))
}
