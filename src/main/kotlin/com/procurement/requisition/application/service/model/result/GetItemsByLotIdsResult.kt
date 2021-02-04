package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import java.math.BigDecimal

data class GetItemsByLotIdsResult(
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
    ) {
        data class Classification(
            val id: String,
            val scheme: ClassificationScheme,
            val description: String
        )

        data class Unit(
            val id: String,
            val name: String
        )
    }
}