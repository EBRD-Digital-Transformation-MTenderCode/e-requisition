package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.GetItemsByLotIdsErrors
import com.procurement.requisition.application.service.model.command.GetItemsByLotIdsCommand
import com.procurement.requisition.application.service.model.result.GetItemsByLotIdsResult
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service

@Service
class GetItemsByLotIdsService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetItemsByLotIdsCommand): Result<GetItemsByLotIdsResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return GetItemsByLotIdsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val receivedLotIds = command.lots.toSet { it.id }
        val itemsByLots = pcr.tender.items.groupBy { it.relatedLot }

        val lotsWithoutRelatedItems = receivedLotIds.subtract(itemsByLots.keys)
        if (lotsWithoutRelatedItems.isNotEmpty())
            return GetItemsByLotIdsErrors.NoItemsFoundForLots(lotsWithoutRelatedItems).asFailure()

        val items = receivedLotIds.flatMap { itemsByLots.getValue(it) }

        return Result.success(generateResult(items))
    }

    private fun generateResult(items: List<Item>) =
        GetItemsByLotIdsResult(
            items = items.map { item ->
                GetItemsByLotIdsResult.Item(
                    id = item.id,
                    relatedLot = item.relatedLot,
                    description = item.description,
                    unit = item.unit.let { unit ->
                        GetItemsByLotIdsResult.Item.Unit(
                            id = unit.id,
                            name = unit.name
                        )
                    },
                    quantity = item.quantity,
                    classification = item.classification.let { classification ->
                        GetItemsByLotIdsResult.Item.Classification(
                            id = classification.id,
                            description = classification.description,
                            scheme = classification.scheme,
                        )
                    },
                    internalId = item.internalId
                )
            }
        )
}
