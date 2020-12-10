package com.procurement.requisition.application.service.set

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.set.error.SetTenderStatusUnsuccessfulErrors
import com.procurement.requisition.application.service.set.model.SetTenderStatusUnsuccessfulCommand
import com.procurement.requisition.application.service.set.model.SetTenderStatusUnsuccessfulResult
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
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
) {

    fun set(command: SetTenderStatusUnsuccessfulCommand): Result<SetTenderStatusUnsuccessfulResult, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return SetTenderStatusUnsuccessfulErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

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

        val json = pcrSerializer.build(updatedPCR).onFailure { return it }
        val state = TenderState(status = updatedPCR.tender.status, statusDetails = updatedPCR.tender.statusDetails)
        pcrRepository.update(
            cpid = command.cpid,
            ocid = command.ocid,
            state = state,
            data = json
        ).onFailure { return it }

        return updatedPCR.convert(idsActiveLots).asSuccess()
    }

    fun List<Lot>.setStatusUnsuccessful(activeLotIds: Set<LotId>) = map { lot ->
        if (lot.id in activeLotIds)
            lot.copy(
                status = LotStatus.UNSUCCESSFUL,
                statusDetails = LotStatusDetails.NONE
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
