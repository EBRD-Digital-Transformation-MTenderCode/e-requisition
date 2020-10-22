package com.procurement.requisition.domain.model.tender.item

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.unit.Unit
import java.math.BigDecimal

data class Item(
    override val id: ItemId,
    val internalId: String?,
    val description: String,
    val quantity: BigDecimal,
    val classification: Classification,
    val unit: Unit,
    val relatedLot: LotId
) : EntityBase<ItemId>()
