package com.procurement.requisition.application.service.find.items

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.find.items.error.FindItemsByLotIdsErrors
import com.procurement.requisition.application.service.find.items.model.FindItemsByLotIdsCommand
import com.procurement.requisition.application.service.find.items.model.FindItemsByLotIdsResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import org.springframework.stereotype.Service
import com.procurement.requisition.infrastructure.handler.v2.converter.ToFindItemsByLotIdsResultConverter.Item as ItemConverter

@Service
class FindItemsByLotIdsService(
    val pcrRepository: PCRRepository,
    val pcrDeserializer: PCRDeserializer
) {

    fun find(command: FindItemsByLotIdsCommand): Result<FindItemsByLotIdsResult, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
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
