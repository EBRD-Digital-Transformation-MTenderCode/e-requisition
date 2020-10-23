package com.procurement.requisition.domain.model.tender.lot

class RelatedLots(values: List<LotId> = emptyList()) : List<LotId> by values {

    constructor(lotId: LotId) : this(listOf(lotId))
}
