package com.procurement.requisition.domain.model.tender.conversion

class Conversions(values: List<Conversion> = emptyList()) : List<Conversion> by values {

    constructor(conversion: Conversion) : this(listOf(conversion))
}
