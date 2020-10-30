package com.procurement.requisition.domain.model.tender.lot

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.Value

data class Lot(
    override val id: LotId,
    val internalId: String?,
    val title: String,
    val description: String?,
    val status: LotStatus,
    val statusDetails: LotStatusDetails,
    val classification: Classification,
    val variants: Variants,
    val value: Value?
) : EntityBase<LotId>()
