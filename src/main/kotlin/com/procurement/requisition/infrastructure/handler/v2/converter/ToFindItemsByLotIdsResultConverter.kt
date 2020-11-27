package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.find.items.model.FindItemsByLotIdsResult
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.unit.Unit
import com.procurement.requisition.domain.model.tender.item.Item as DomainItem

object ToFindItemsByLotIdsResultConverter {
    object Item {
        fun fromDomain(item: DomainItem): FindItemsByLotIdsResult.Tender.Item =
            FindItemsByLotIdsResult.Tender.Item(
                id = item.id,
                internalId = item.internalId,
                description = item.description,
                quantity = item.quantity,
                classification = item.classification.convert(),
                unit = item.unit.convert(),
                relatedLot = item.relatedLot
            )

        fun Classification.convert(): FindItemsByLotIdsResult.Tender.Classification =
            FindItemsByLotIdsResult.Tender.Classification(
                id = this.id,
                scheme = this.scheme,
                description = this.description
            )

        fun Unit.convert(): FindItemsByLotIdsResult.Tender.Unit =
            FindItemsByLotIdsResult.Tender.Unit(
                id = this.id,
                name = this.name
            )
    }
}
