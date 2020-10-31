package com.procurement.requisition.domain.model.tender.item

class Items(values: List<Item> = emptyList()) : List<Item> by values {

    constructor(item: Item) : this(listOf(item))
}
