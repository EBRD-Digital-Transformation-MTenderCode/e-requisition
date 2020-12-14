package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.FindItemsByLotIdsErrors
import com.procurement.requisition.application.service.model.command.FindItemsByLotIdsCommand
import com.procurement.requisition.application.service.model.result.FindItemsByLotIdsResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import org.springframework.stereotype.Service
import com.procurement.requisition.infrastructure.handler.v2.converter.ToFindItemsByLotIdsResultConverter.Item as ItemConverter

@Service
class FindItemsByLotIdsService(
    private val pcrManagement: PCRManagementService,
) {

    fun find(command: FindItemsByLotIdsCommand): Result<FindItemsByLotIdsResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return FindItemsByLotIdsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val targetLots = command.tender.lots.toSet()

        val foundedItems = pcr.tender.items.filter { item -> item.relatedLot in targetLots }

        val result = FindItemsByLotIdsResult(
            tender = FindItemsByLotIdsResult.Tender(
                items = foundedItems.map { item -> ItemConverter.fromDomain(item) }
            )
        )

        return Result.success(result)
    }
}
