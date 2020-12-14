package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CheckLotAwardedErrors
import com.procurement.requisition.application.service.model.command.CheckLotAwardedCommand
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckLotAwardedService(
    private val pcrManagement: PCRManagementService,
) {

    fun check(command: CheckLotAwardedCommand): Validated<Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckLotAwardedErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        pcr.tender.lots
            .let { lots ->
                val foundedLot = lots.find { lot -> lot.id == command.lotId }
                    ?: return CheckLotAwardedErrors.Lot.NotFound(id = command.lotId).asValidatedError()

                validateState(foundedLot.status, foundedLot.statusDetails)
            }
            .onFailure { return it }

        return Validated.ok()
    }

    fun validateState(
        status: LotStatus,
        statusDetails: LotStatusDetails
    ): Validated<CheckLotAwardedErrors.Lot.InvalidState> =
        if (status == LotStatus.ACTIVE && statusDetails != LotStatusDetails.AWARDED)
            Validated.ok()
        else
            CheckLotAwardedErrors.Lot.InvalidState(status = status, statusDetails = statusDetails)
                .asValidatedError()
}
