package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.model.result.GetItemsByLotIdsResult
import com.procurement.requisition.infrastructure.handler.v1.model.response.GetItemsByLotIdsResponse

fun GetItemsByLotIdsResult.convert() = GetItemsByLotIdsResponse(
    items = items.map { item ->
        GetItemsByLotIdsResponse.Item(
            id = item.id.underlying,
            relatedLot = item.relatedLot.underlying,
            description = item.description,
            unit = item.unit.let { unit ->
                GetItemsByLotIdsResponse.Item.Unit(
                    id = unit.id,
                    name = unit.name
                )
            },
            quantity = item.quantity,
            classification = item.classification.let { classification ->
                GetItemsByLotIdsResponse.Item.Classification(
                    id = classification.id,
                    description = classification.description,
                    scheme = classification.scheme.toString(),
                )
            },
            internalId = item.internalId
        )
    }
)
