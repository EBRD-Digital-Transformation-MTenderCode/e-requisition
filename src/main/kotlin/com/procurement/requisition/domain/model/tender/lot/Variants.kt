package com.procurement.requisition.domain.model.tender.lot

class Variants(values: List<Variant> = emptyList()) : List<Variant> by values {

    constructor(variant: Variant) : this(listOf(variant))
}
