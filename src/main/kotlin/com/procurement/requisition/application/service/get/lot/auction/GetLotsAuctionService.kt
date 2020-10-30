package com.procurement.requisition.application.service.get.lot.auction

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.get.lot.auction.model.GetLotsAuctionCommand
import com.procurement.requisition.application.service.get.lot.auction.model.GetLotsAuctionResult
import com.procurement.requisition.application.service.get.lot.auction.model.ToGetLotsAuctionResultConverter
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetLotsAuctionService(
    val pcrRepository: PCRRepository,
    val pcrDeserializer: PCRDeserializer
) {

    fun get(command: GetLotsAuctionCommand): Result<GetLotsAuctionResult, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return GetLotsAuctionErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val activeLots = pcr.tender.lots
            .filter { lot -> lot.status == LotStatus.ACTIVE }
            .takeIf { it.isNotEmpty() }
            ?.map { lot -> ToGetLotsAuctionResultConverter.fromDomain(lot) }
            ?: return GetLotsAuctionErrors.NoActiveLotsFound(cpid = command.cpid, ocid = command.ocid).asFailure()


        return GetLotsAuctionResult(
            tender = GetLotsAuctionResult.Tender(
                id = pcr.tender.id,
                title = pcr.tender.title,
                description = pcr.tender.description,
                lots = activeLots
            )
        ).asSuccess()
    }
}
