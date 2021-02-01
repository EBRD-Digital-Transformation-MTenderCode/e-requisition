package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.SetUnsuccessfulStateForLotsErrors
import com.procurement.requisition.application.service.model.command.SetUnsuccessfulStateForLotsCommand
import com.procurement.requisition.application.service.model.result.SetUnsuccessfulStateForLotsResult
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.functional.asValidatedError
import com.procurement.requisition.lib.getUnknownElements
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service

@Service
class SetUnsuccessfulStateForLotsService(
    private val pcrManagement: PCRManagementService,
) {

    fun set(command: SetUnsuccessfulStateForLotsCommand): Result<SetUnsuccessfulStateForLotsResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetUnsuccessfulStateForLotsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asFailure()

        val tender = pcr.tender
        val receivedLotIds: Set<LotId> = command.tender.lots.toSet { it.id }
        val storedLotIds = tender.lots.toSet { it.id }

        checkLotIds(receivedLotIds = receivedLotIds, storedLotIds = storedLotIds)
            .onFailure { return it.reason.asFailure() }

        val updatedLots = tender.lots.setState(ids = receivedLotIds)
        val updatedPCR = pcr.copy(
            tender = tender.copy(
                lots = updatedLots
            )
        )

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPCR)
            .onFailure { return it }

        return updatedLots.convert(receivedLotIds).asSuccess()
    }

    private fun checkLotIds(
        receivedLotIds: Set<LotId>,
        storedLotIds: Set<LotId>
    ): Validated<SetUnsuccessfulStateForLotsErrors.UnknownLot> {
        val unknownLotIds = getUnknownElements(received = receivedLotIds, known = storedLotIds)
        return if (unknownLotIds.isNotEmpty())
            SetUnsuccessfulStateForLotsErrors.UnknownLot(lotIds = unknownLotIds).asValidatedError()
        else
            Validated.ok()
    }

    private fun List<Lot>.setState(ids: Set<LotId>) = this
        .map { lot ->
            if (lot.id in ids) {
                lot.copy(
                    status = LotStatus.UNSUCCESSFUL,
                    statusDetails = LotStatusDetails.ALL_REJECTED
                )
            } else
                lot
        }
        .let { Lots(it) }

    private fun List<Lot>.convert(updatedLotIds: Set<LotId>) = SetUnsuccessfulStateForLotsResult(
        tender = SetUnsuccessfulStateForLotsResult.Tender(
            lots = this
                .asSequence()
                .filter { lot -> lot.id in updatedLotIds }
                .map { lot ->
                    SetUnsuccessfulStateForLotsResult.Tender.Lot(
                        id = lot.id,
                        status = lot.status,
                        statusDetails = lot.statusDetails
                    )
                }
                .toList()
        )
    )
}
