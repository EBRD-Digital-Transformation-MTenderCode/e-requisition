package com.procurement.requisition.application.service.set.tender

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.set.tender.model.SetTenderUnsuspendedCommand
import com.procurement.requisition.application.service.set.tender.model.SetTenderUnsuspendedResult
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class SetTenderUnsuspendedService(
    private val pcrManagement: PCRManagementService,
) {

    fun set(command: SetTenderUnsuspendedCommand): Result<SetTenderUnsuspendedResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return SetTenderUnsuspendedErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        if (pcr.tender.statusDetails != TenderStatusDetails.SUSPENDED)
            return SetTenderUnsuspendedErrors.TenderNotUnsuspendedError().asFailure()

        val statusDetails = TenderStatusDetails.orNull(command.phase)
            ?: return SetTenderUnsuspendedErrors.TenderStatusDetailsParseFailed(command.phase).asFailure()
        val updatedPcr = pcr.copy(tender = pcr.tender.copy(statusDetails = statusDetails))

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPcr)
            .onFailure { return it }

        return SetTenderUnsuspendedResult(
            SetTenderUnsuspendedResult.Tender(
                updatedPcr.tender.status,
                updatedPcr.tender.statusDetails,
                updatedPcr.tender.procurementMethodModalities,
            )
        ).asSuccess()
    }
}
