package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.SetTenderStatusSuspendedErrors
import com.procurement.requisition.application.service.model.command.SetTenderStatusSuspendedCommand
import com.procurement.requisition.application.service.model.result.SetTenderStatusSuspendedResult
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderStatusSuspendedService(
    private val pcrManagement: PCRManagementService,
) {

    fun set(command: SetTenderStatusSuspendedCommand): Result<SetTenderStatusSuspendedResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetTenderStatusSuspendedErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val updatedPcr = pcr.copy(tender = pcr.tender.copy(statusDetails = TenderStatusDetails.SUSPENDED))
        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPcr)

        return SetTenderStatusSuspendedResult(
            SetTenderStatusSuspendedResult.Tender(
                status = updatedPcr.tender.status,
                statusDetails = updatedPcr.tender.statusDetails
            )
        ).asSuccess()
    }
}
