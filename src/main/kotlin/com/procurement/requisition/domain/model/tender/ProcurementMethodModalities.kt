package com.procurement.requisition.domain.model.tender

class ProcurementMethodModalities(
    values: List<ProcurementMethodModality> = emptyList()
) : List<ProcurementMethodModality> by values {

    constructor(value: ProcurementMethodModality) : this(listOf(value))
}
