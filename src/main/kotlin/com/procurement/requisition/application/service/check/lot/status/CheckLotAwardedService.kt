package com.procurement.requisition.application.service.check.lot.status

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.check.lot.status.error.CheckLotAwardedErrors
import com.procurement.requisition.application.service.check.lot.status.model.CheckLotAwardedCommand
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckLotAwardedService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
) {

    fun check(command: CheckLotAwardedCommand): Validated<Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it.reason.asValidatedError() }
            ?: return CheckLotAwardedErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        pcr.tender.lots
            .let { lots ->
                val foundedLot = lots.find { lot -> lot.id == command.lotId }
                    ?: return CheckLotAwardedErrors.Lot.NotFound(id = command.lotId).asValidatedError()

                validateState(foundedLot.status, foundedLot.statusDetails)
            }
            .onFailure { return it.reason.asValidatedError() }

        return Validated.ok()
    }

    fun validateState(status: LotStatus, statusDetails: LotStatusDetails): Validated<CheckLotAwardedErrors.Lot.InvalidState> =
        if (status == LotStatus.ACTIVE && statusDetails != LotStatusDetails.AWARDED)
            Validated.ok()
        else
            CheckLotAwardedErrors.Lot.InvalidState(status = status, statusDetails = statusDetails)
                .asValidatedError()
}
