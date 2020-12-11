package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.classification.ClassificationId
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.unit.UnitId
import java.math.BigDecimal

data class FindItemsByLotIdsResult(
    val tender: Tender
) {
    data class Tender(
        val items: List<Item>
    ) {
        data class Item(
            val id: ItemId,
            val internalId: String?,
            val description: String,
            val quantity: BigDecimal,
            val classification: Classification,
            val unit: Unit,
            val relatedLot: LotId
        )

        data class Classification(
            val id: ClassificationId,
            val scheme: ClassificationScheme,
            val description: String
        )

        data class Unit(val id: UnitId, val name: String)
    }
}
