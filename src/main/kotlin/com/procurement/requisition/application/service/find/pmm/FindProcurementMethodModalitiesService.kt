package com.procurement.requisition.application.service.find.pmm

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.find.pmm.error.FindProcurementMethodModalitiesErrors
import com.procurement.requisition.application.service.find.pmm.model.FindProcurementMethodModalitiesCommand
import com.procurement.requisition.application.service.find.pmm.model.FindProcurementMethodModalitiesResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import org.springframework.stereotype.Service

@Service
class FindProcurementMethodModalitiesService(
    private val pcrManagement: PCRManagementService,
) {

    fun find(command: FindProcurementMethodModalitiesCommand): Result<FindProcurementMethodModalitiesResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return FindProcurementMethodModalitiesErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asFailure()

        val targetPmm = command.tender.procurementMethodModalities.toSet()
        val foundedPmm = pcr.tender.procurementMethodModalities.filter { it in targetPmm }

        val result = FindProcurementMethodModalitiesResult(
            tender = FindProcurementMethodModalitiesResult.Tender(
                procurementMethodModalities = foundedPmm
            )
        )

        return Result.success(result)
    }
}
