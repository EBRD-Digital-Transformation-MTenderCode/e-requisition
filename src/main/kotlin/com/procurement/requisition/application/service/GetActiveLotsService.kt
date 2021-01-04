package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.GetActiveLotsErrors
import com.procurement.requisition.application.service.model.result.GetActiveLotsResult
import com.procurement.requisition.application.service.model.command.GetActiveLotsCommand
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetActiveLotsService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetActiveLotsCommand): Result<GetActiveLotsResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return GetActiveLotsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        return pcr.tender.lots.asSequence()
            .filter { lot -> lot.status == LotStatus.ACTIVE }
            .map { lot -> GetActiveLotsResult.Lot(id = lot.id) }
            .toList()
            .let { lots -> GetActiveLotsResult(lots) }
            .asSuccess()
    }
}
