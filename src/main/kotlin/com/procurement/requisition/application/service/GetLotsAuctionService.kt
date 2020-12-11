package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.model.command.GetLotsAuctionCommand
import com.procurement.requisition.application.service.model.result.GetLotsAuctionResult
import com.procurement.requisition.application.service.converter.ToGetLotsAuctionResultConverter
import com.procurement.requisition.application.service.error.GetLotsAuctionErrors
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetLotsAuctionService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetLotsAuctionCommand): Result<GetLotsAuctionResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
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
