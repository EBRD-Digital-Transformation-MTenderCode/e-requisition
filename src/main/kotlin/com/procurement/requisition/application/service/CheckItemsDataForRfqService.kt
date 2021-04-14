package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CheckItemsDataForRfqErrors
import com.procurement.requisition.domain.model.classification.ClassificationId
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.LotId
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

        //there is always will be only one lot
        val receivedLot = command.tender.lots.toSet { it.id }.first()
        val receivedItems = command.tender.items
        val storedLotsIds = pcr.tender.lots.toSet { it.id }

        checkLotsExists(receivedLot, storedLotsIds).onFailure { return it }
        checkItemsData(receivedLot, receivedItems, pcr.tender.items).onFailure { return it }

        return Validated.ok()
    }

    private fun checkItemsData(receivedLot: LotId, receivedItems: List<Command.Tender.Item>, storedItems: Items): Validated<CheckItemsDataForRfqErrors> {
        val storedItemsForLot = storedItems
            .groupBy { it.relatedLot }
            .getValue(receivedLot)
            .associateBy { it.classification.id }

        checkClassifications(receivedItems, storedItemsForLot).onFailure { return it }
        checkItemQuantity(receivedItems, storedItemsForLot).onFailure { return it }
        checkItemUnit(receivedItems, storedItemsForLot).onFailure { return it }

        return Validated.ok()
    }

    private fun checkItemQuantity(receivedItems: List<Command.Tender.Item>, storedItems: Map<ClassificationId, Item>): Validated<CheckItemsDataForRfqErrors> {
        receivedItems.forEach { receivedItem ->
            val storedItem = storedItems.getValue(receivedItem.classification.id)
            if (receivedItem.quantity > storedItem.quantity)
                return CheckItemsDataForRfqErrors.QuantityMismatch(receivedItem.id, storedItem.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkItemUnit(receivedItems: List<Command.Tender.Item>, storedItems: Map<ClassificationId, Item>): Validated<CheckItemsDataForRfqErrors> {
        receivedItems.forEach { receivedItem ->
            val storedItem = storedItems.getValue(receivedItem.classification.id)
            if (receivedItem.unit.id != storedItem.unit.id)
                return CheckItemsDataForRfqErrors.UnitMismatch(receivedItem.id, storedItem.id).asValidatedError()
        }

        return Validated.ok()
    }

    private fun checkClassifications(receivedItems: List<Command.Tender.Item>, storedItemsForLot: Map<ClassificationId, Item>): Validated<CheckItemsDataForRfqErrors> {
        val storedItemsClassification = storedItemsForLot.keys
        val receivedItemsClassification = receivedItems.toSet { it.classification.id }

        if (!storedItemsClassification.containsAll(receivedItemsClassification))
            return CheckItemsDataForRfqErrors.ClassificationMismatch().asValidatedError()

        return Validated.ok()
    }


    private fun checkLotsExists(receivedLots: LotId, storedLots: Set<LotId>): Validated<CheckItemsDataForRfqErrors> {
        if (receivedLots !in storedLots)
            return CheckItemsDataForRfqErrors.LotNotFound(receivedLots).asValidatedError()

        return Validated.ok()
    }
}
