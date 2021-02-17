package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.SetTenderStatusUnsuccessfulErrors
import com.procurement.requisition.application.service.model.command.SetTenderStatusUnsuccessfulCommand
import com.procurement.requisition.application.service.model.result.SetTenderStatusUnsuccessfulResult
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderStatusUnsuccessfulService(
    private val pcrManagement: PCRManagementService,
) {

    fun set(command: SetTenderStatusUnsuccessfulCommand): Result<SetTenderStatusUnsuccessfulResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetTenderStatusUnsuccessfulErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asFailure()

        val tender = pcr.tender
        val idsActiveLots = tender.lots.asSequence()
            .filter { lot -> lot.status == LotStatus.ACTIVE }
            .map { lot -> lot.id }
            .toSet()

        val updatedLots = tender.lots.setStatusUnsuccessful(idsActiveLots)
        val updatedPCR = pcr.copy(
            tender = tender.copy(
                status = TenderStatus.UNSUCCESSFUL,
                statusDetails = TenderStatusDetails.EMPTY,
                lots = updatedLots
            )
        )

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPCR)
            .onFailure { return it }

        return updatedPCR.convert(idsActiveLots).asSuccess()
    }

    fun List<Lot>.setStatusUnsuccessful(activeLotIds: Set<LotId>) = map { lot ->
        if (lot.id in activeLotIds)
            lot.copy(
                status = LotStatus.UNSUCCESSFUL,
                statusDetails = null
            )
        else
            lot
    }
        .let { Lots(it) }

    fun PCR.convert(updatedLotIds: Set<LotId>) = SetTenderStatusUnsuccessfulResult(
        tender = SetTenderStatusUnsuccessfulResult.Tender(
            status = tender.status,
            statusDetails = tender.statusDetails,
            lots = tender.lots
                .asSequence()
                .filter { lot -> lot.id in updatedLotIds }
                .map { lot ->
                    SetTenderStatusUnsuccessfulResult.Tender.Lot(
                        id = lot.id,
                        status = lot.status
                    )
                }
                .toList()
        )
    )
}
