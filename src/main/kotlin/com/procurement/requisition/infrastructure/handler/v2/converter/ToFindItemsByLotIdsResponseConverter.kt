package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.result.FindItemsByLotIdsResult
import com.procurement.requisition.infrastructure.handler.v2.model.response.FindItemsByLotIdsResponse

fun FindItemsByLotIdsResult.convert(): FindItemsByLotIdsResponse =
    FindItemsByLotIdsResponse(
        tender = this.tender.let {
            FindItemsByLotIdsResponse.Tender(
                items = tender.items.map { item ->
                    FindItemsByLotIdsResponse.Tender.Item(
                        id = item.id.underlying,
                        internalId = item.internalId,
                        description = item.description,
                        quantity = item.quantity,
                        classification = item.classification.convert(),
                        unit = item.unit.convert(),
                        relatedLot = item.relatedLot.underlying
                    )
                }
            )
        }
    )

private fun FindItemsByLotIdsResult.Tender.Classification.convert(): FindItemsByLotIdsResponse.Tender.Classification =
    FindItemsByLotIdsResponse.Tender.Classification(
        id = this.id,
        scheme = this.scheme,
        description = this.description
    )

private fun FindItemsByLotIdsResult.Tender.Unit.convert(): FindItemsByLotIdsResponse.Tender.Unit =
    FindItemsByLotIdsResponse.Tender.Unit(
        id = this.id,
        name = this.name
    )
