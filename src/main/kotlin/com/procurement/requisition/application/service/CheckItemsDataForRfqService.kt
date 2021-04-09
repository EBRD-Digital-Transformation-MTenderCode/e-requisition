package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CheckItemsDataForRfqErrors
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service
import com.procurement.requisition.application.service.model.command.CheckItemsDataForRfqCommand as Command

@Service
class CheckItemsDataForRfqService(private val pcrManagement: PCRManagementService) {

    fun check(command: Command): Validated<Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckItemsDataForRfqErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        val receivedItems = command.tender.items
        val receivedLots = command.tender.lots.toSet { it.id }

        val storedLotsIds = pcr.tender.lots.toSet { it.id }

        checkLotsExists(receivedLots, storedLotsIds).onFailure { return it }
        checkLotsRelatedWithItems(storedLotsIds, receivedItems).onFailure { return it }
        checkClassifications(receivedItems, pcr.tender.lots).onFailure { return it }
        checkItemQuantity(receivedItems, pcr.tender.items).onFailure { return it }
        checkItemUnit(receivedItems, pcr.tender.items).onFailure { return it }

        return Validated.ok()
    }

    private fun checkItemUnit(receivedItems: List<Command.Tender.Item>, storedItems: Items): Validated<CheckItemsDataForRfqErrors> {
        val storedItemsById = storedItems.associateBy { it.id }

        receivedItems.forEach { receivedItem ->
            val storedItem = storedItemsById.getValue(receivedItem.id)
            if (receivedItem.unit.id != storedItem.unit.id)
                return CheckItemsDataForRfqErrors.UnitMismatch(receivedItem.id, storedItem.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkItemQuantity(receivedItems: List<Command.Tender.Item>, storedItems: Items): Validated<CheckItemsDataForRfqErrors> {
        val storedItemsById = storedItems.associateBy { it.id }

        receivedItems.forEach { receivedItem ->
            val storedItem = storedItemsById.getValue(receivedItem.id)
            if (receivedItem.quantity > storedItem.quantity)
                return CheckItemsDataForRfqErrors.QuantityMismatch(receivedItem.id, storedItem.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkClassifications(items: List<Command.Tender.Item>, storedLots: Lots): Validated<CheckItemsDataForRfqErrors> {
        val storedLotsByIds = storedLots.associateBy { it.id }

        items.forEach { item ->
            val relatedLot = storedLotsByIds.getValue(item.relatedLot)

            if (item.classification.id != relatedLot.classification.id || item.classification.scheme != relatedLot.classification.scheme)
                return CheckItemsDataForRfqErrors.ClassificationMismatch(item.id, relatedLot.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkLotsRelatedWithItems(storedLotsIds: Set<LotId>, items: List<Command.Tender.Item>): Validated<CheckItemsDataForRfqErrors> {
        items.forEach { item ->
            if (item.relatedLot !in storedLotsIds)
                return CheckItemsDataForRfqErrors.InvalidRelatedLot(item.id, item.relatedLot).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkLotsExists(receivedLots: Collection<LotId>, storedLots: Set<LotId>): Validated<CheckItemsDataForRfqErrors> {
        receivedLots.forEach { receivedLotId ->
            if (receivedLotId !in storedLots)
                return CheckItemsDataForRfqErrors.LotNotFound(receivedLotId).asValidatedError()
        }

        return Validated.ok()
    }
}
