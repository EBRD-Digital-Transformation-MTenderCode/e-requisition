package com.procurement.requisition.application.service.check.lot.status

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.check.lot.status.error.CheckLotsStatusErrors
import com.procurement.requisition.application.service.check.lot.status.model.CheckLotsStatusCommand
import com.procurement.requisition.domain.model.tender.Tender
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckLotsStatusService(
    private val pcrManagement: PCRManagementService,
) {

    fun check(command: CheckLotsStatusCommand): Validated<Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckLotsStatusErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        pcr.tender
            .findLot(id = command.relatedLot)
            .onFailure { return it.reason.asValidatedError() }
            .checkStatus()
            .onFailure { return it }

        return Validated.ok()
    }

    fun Tender.findLot(id: LotId): Result<Lot, CheckLotsStatusErrors.Lot.NotFound> = lots.find { lot -> lot.id == id }
        ?.asSuccess()
        ?: CheckLotsStatusErrors.Lot.NotFound(id = id).asFailure()

    fun Lot.checkStatus(): Validated<CheckLotsStatusErrors.Lot.InvalidStatus> =
        if (status != LotStatus.ACTIVE)
            CheckLotsStatusErrors.Lot.InvalidStatus(id = id, status = status).asValidatedError()
        else
            Validated.ok()
}
