package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CheckItemsDataForRfqErrors
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.success
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service
import com.procurement.requisition.application.service.model.command.CheckItemsDataForRfqCommand as Command

@Service
class CheckItemsDataForRfqService(private val pcrManagement: PCRManagementService) {

    fun check(command: Command): Validated<Failure> {
        val receivedLotId= getLotsCount(command.tender.lots).onFailure { return it.reason.asValidatedError() }

        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckItemsDataForRfqErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        checkLotsExists(receivedLotId, pcr.tender.lots).onFailure { return it }
        checkItemsData(receivedLotId, command.tender.items, pcr.tender.items).onFailure { return it }

        return Validated.ok()
    }

    private fun getLotsCount(lots: List<Command.Tender.Lot>): Result<LotId, CheckItemsDataForRfqErrors> =
        if (lots.size != 1)
            CheckItemsDataForRfqErrors.InvalidLotsCount().asFailure()
        else
            success(lots.first().id)

    private fun checkItemsData(receivedLotId: LotId, receivedItems: List<Command.Tender.Item>, storedItems: Items): Validated<CheckItemsDataForRfqErrors> {
        val storedItemsForLot = storedItems
            .filter { it.relatedLot == receivedLotId }

        val storedItemsByReceivedId = getStoredItemsByReceived(receivedItems, storedItemsForLot).onFailure { return it.reason.asValidatedError() }
        checkItemQuantity(receivedItems, storedItemsByReceivedId).onFailure { return it }
        checkItemUnit(receivedItems, storedItemsByReceivedId).onFailure { return it }

        return Validated.ok()
    }

    private fun checkItemQuantity(receivedItems: List<Command.Tender.Item>, storedItemsByReceivedId: Map<ItemId, Item>): Validated<CheckItemsDataForRfqErrors> {
        receivedItems.forEach { receivedItem ->
            val storedItem = storedItemsByReceivedId.getValue(receivedItem.id)
            if (receivedItem.quantity > storedItem.quantity)
                return CheckItemsDataForRfqErrors.QuantityMismatch(receivedItem = receivedItem.id, storedItem = storedItem.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkItemUnit(receivedItems: List<Command.Tender.Item>, storedItemsByReceivedId: Map<ItemId, Item>): Validated<CheckItemsDataForRfqErrors> {
        receivedItems.forEach { receivedItem ->
            val storedItem = storedItemsByReceivedId.getValue(receivedItem.id)
            if (receivedItem.unit.id != storedItem.unit.id)
                return CheckItemsDataForRfqErrors.UnitMismatch(receivedItem = receivedItem.id, storedItem = storedItem.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun getStoredItemsByReceived(receivedItems: List<Command.Tender.Item>, storedItems: List<Item>): Result<Map<ItemId, Item>, CheckItemsDataForRfqErrors> {
        val storedItemsByClassification = storedItems.asSequence()
            .associateBy { it.classification.id to it.classification.scheme }

        return receivedItems
            .associate { receivedItem ->
                val key = receivedItem.classification.id to receivedItem.classification.scheme
                val targetItem = storedItemsByClassification[key]
                    ?: return CheckItemsDataForRfqErrors.ClassificationMismatch().asFailure()
                receivedItem.id to targetItem
            }
            .asSuccess()
    }

    private fun checkLotsExists(receivedLotId: LotId, storedLots: Lots): Validated<CheckItemsDataForRfqErrors> {
        val existenceLot = storedLots.find { storedLot -> storedLot.id == receivedLotId }

        return if (existenceLot == null)
            CheckItemsDataForRfqErrors.LotNotFound(receivedLotId).asValidatedError()
        else
            Validated.ok()
    }
}
