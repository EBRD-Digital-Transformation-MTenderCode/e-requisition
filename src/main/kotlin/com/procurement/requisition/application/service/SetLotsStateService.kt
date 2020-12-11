package com.procurement.requisition.application.service

import com.procurement.requisition.application.repository.rule.model.LotStateForSettingRule
import com.procurement.requisition.application.service.rule.RulesService
import com.procurement.requisition.application.service.error.SetLotsStateErrors
import com.procurement.requisition.application.service.model.command.SetLotsStateCommand
import com.procurement.requisition.application.service.model.result.SetLotsStateResult
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
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
class SetLotsStateService(
    private val pcrManagement: PCRManagementService,
    private val rulesService: RulesService
) {

    fun set(command: SetLotsStateCommand): Result<SetLotsStateResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetLotsStateErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val tender = pcr.tender
        val receivedLotIds: Set<LotId> = command.tender.lots.toSet { it.id }
        val storedLotIds = tender.lots.toSet { it.id }

        // VR.COM-17.12.2
        checkLotIds(receivedLotIds = receivedLotIds, storedLotIds = storedLotIds)
            .onFailure { return it.reason.asFailure() }

        val lotState = rulesService
            .getLotState(country = command.country, pmd = command.pmd, operationType = command.operationType)
            .onFailure { return it }

        val updatedLots = tender.lots.setStatus(ids = receivedLotIds, state = lotState)
        val updatedPCR = pcr.copy(
            tender = tender.copy(
                lots = updatedLots
            )
        )

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPCR)
            .onFailure { return it }

        return updatedLots.convert(receivedLotIds).asSuccess()
    }

    /**
     * VR.COM-17.12.2
     */
    fun checkLotIds(receivedLotIds: Set<LotId>, storedLotIds: Set<LotId>): Validated<SetLotsStateErrors.UnknownLot> {
        val unknownLotIds = getUnknownElements(received = receivedLotIds, known = storedLotIds)
        return if (unknownLotIds.isNotEmpty())
            SetLotsStateErrors.UnknownLot(lotIds = unknownLotIds).asValidatedError()
        else
            Validated.ok()
    }

    fun List<Lot>.setStatus(ids: Set<LotId>, state: LotStateForSettingRule) = this
        .map { lot ->
            if (lot.id in ids) {
                lot.copy(
                    status = state.status,
                    statusDetails = state.statusDetails ?: lot.statusDetails
                )
            } else
                lot
        }
        .let { Lots(it) }

    fun List<Lot>.convert(updatedLotIds: Set<LotId>) = SetLotsStateResult(
        tender = SetLotsStateResult.Tender(
            lots = this
                .asSequence()
                .filter { lot -> lot.id in updatedLotIds }
                .map { lot ->
                    SetLotsStateResult.Tender.Lot(
                        id = lot.id,
                        status = lot.status,
                        statusDetails = lot.statusDetails
                    )
                }
                .toList()
        )
    )
}
