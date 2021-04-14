package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import java.math.BigDecimal

data class CheckItemsDataForRfqCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender
) {

    data class Tender(
        val lots: List<Lot>,
        val items: List<Item>
    ) {

        data class Lot(val id: LotId)

        data class Item(
            val id: ItemId,
            val classification: Classification,
            val quantity: BigDecimal,
            val unit: Unit
        ) {
            data class Unit(val id: String)

            data class Classification(val id: String, val scheme: ClassificationScheme)
        }
    }
}
