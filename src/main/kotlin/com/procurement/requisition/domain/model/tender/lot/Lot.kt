package com.procurement.requisition.domain.model.tender.lot

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.Classification

data class Lot(
    override val id: LotId,
    val internalId: String?,
    val title: String,
    val description: String?,
    val status: LotStatus,
    val statusDetails: LotStatusDetails,
    val classification: Classification,
    val variants: Variant
) : EntityBase<LotId>()
