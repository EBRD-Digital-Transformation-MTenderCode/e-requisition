package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.SetLotsStatusUnsuccessfulErrors
import com.procurement.requisition.application.service.model.command.SetLotsStatusUnsuccessfulCommand
import com.procurement.requisition.application.service.model.result.SetLotsStatusUnsuccessfulResult
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service

@Service
class SetLotsStatusUnsuccessfulService(
    private val pcrManagement: PCRManagementService
) {

    fun set(command: SetLotsStatusUnsuccessfulCommand): Result<SetLotsStatusUnsuccessfulResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetLotsStatusUnsuccessfulErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val idsUnsuccessfulLots: Set<LotId> = command.lots.toSet { it.id }
        val tender = pcr.tender
        val updatedLots = tender.lots.setStatusUnsuccessful(idsUnsuccessfulLots)
        val activeLotsIsPresent = updatedLots.any { it.status == LotStatus.ACTIVE }

        val updatedPCR = pcr.copy(
            tender = tender.copy(
                status = if (activeLotsIsPresent) tender.status else TenderStatus.UNSUCCESSFUL,
                statusDetails = if (activeLotsIsPresent) tender.statusDetails else TenderStatusDetails.EMPTY,
                lots = updatedLots
            )
        )

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPCR)
            .onFailure { return it }

        return updatedPCR.convert(idsUnsuccessfulLots).asSuccess()
    }

    fun List<Lot>.setStatusUnsuccessful(ids: Set<LotId>) = this
        .map { lot ->
            if (lot.id in ids) {
                lot.copy(
                    status = LotStatus.UNSUCCESSFUL
                )
            } else
                lot
        }
        .let { Lots(it) }

    fun PCR.convert(updatedLotIds: Set<LotId>) = SetLotsStatusUnsuccessfulResult(
        tender = SetLotsStatusUnsuccessfulResult.Tender(
            status = tender.status,
            statusDetails = tender.statusDetails,
            lots = tender.lots
                .asSequence()
                .filter { lot -> lot.id in updatedLotIds }
                .map { lot ->
                    SetLotsStatusUnsuccessfulResult.Tender.Lot(
                        id = lot.id,
                        status = lot.status
                    )
                }
                .toList()
        )
    )
}
