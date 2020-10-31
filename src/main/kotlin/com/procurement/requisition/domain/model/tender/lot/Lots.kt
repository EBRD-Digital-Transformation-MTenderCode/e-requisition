package com.procurement.requisition.domain.model.tender.lot

class Lots(values: List<Lot> = emptyList()) : List<Lot> by values {

    constructor(lot: Lot) : this(listOf(lot))
}
