package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CheckItemsDataForRfqErrors
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.success
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service
import com.procurement.requisition.application.service.model.command.CheckItemsDataForRfqCommand as Command

@Service
class CheckItemsDataForRfqService(private val pcrManagement: PCRManagementService) {

    fun check(command: Command): Validated<Failure> {
        val receivedLotId = getLotsCount(command.tender.lots).onFailure { return it.reason.asValidatedError() }

        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckItemsDataForRfqErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asValidatedError()

        checkLotsExists(receivedLotId, pcr.tender.lots).onFailure { return it }

        val storedItemsForLot = pcr.tender.items
            .filter { it.relatedLot == receivedLotId }

        checkReceivedItemsByStored(command.tender.items, storedItemsForLot)
            .onFailure { return it.reason.asValidatedError() }

        return Validated.ok()
    }

    private fun getLotsCount(lots: List<Command.Tender.Lot>): Result<LotId, CheckItemsDataForRfqErrors> =
        if (lots.size != 1)
            CheckItemsDataForRfqErrors.InvalidLotsCount().asFailure()
        else
            success(lots.first().id)

    private fun checkReceivedItemsByStored(
        receivedItems: List<Command.Tender.Item>,
        storedItems: List<Item>
    ): Validated<CheckItemsDataForRfqErrors> {
        val storedItemsByClassification = storedItems.asSequence()
            .associateBy { it.classification.id to it.classification.scheme }

        receivedItems
            .associate { receivedItem ->
                val key = receivedItem.classification.id to receivedItem.classification.scheme
                val targetItem = storedItemsByClassification[key]
                    ?: return CheckItemsDataForRfqErrors.ClassificationMismatch().asValidatedError()
                receivedItem.id to targetItem
            }

        return Validated.ok()
    }

    private fun checkLotsExists(receivedLotId: LotId, storedLots: Lots): Validated<CheckItemsDataForRfqErrors> {
        val existenceLot = storedLots.find { storedLot -> storedLot.id == receivedLotId }

        return if (existenceLot == null)
            CheckItemsDataForRfqErrors.LotNotFound(receivedLotId).asValidatedError()
        else
            Validated.ok()
    }
}
