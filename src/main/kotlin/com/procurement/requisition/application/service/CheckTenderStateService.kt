package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CheckTenderStateErrors
import com.procurement.requisition.application.service.model.command.CheckTenderStateCommand
import com.procurement.requisition.application.service.rule.RulesService
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckTenderStateService(
    private val pcrManagement: PCRManagementService,
    private val rulesService: RulesService,
) {

    fun check(command: CheckTenderStateCommand): Validated<Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?: return CheckTenderStateErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        val tender = pcr.tender

        val tenderStatesRules = rulesService
            .getValidTenderStates(
                country = command.country,
                pmd = command.pmd,
                operationType = command.operationType
            )
            .onFailure { return it.reason.asValidatedError() }

        return if (tenderStatesRules.contains(status = tender.status, statusDetails = tender.statusDetails))
            CheckTenderStateErrors.Tender.InvalidState(status = tender.status, statusDetails = tender.statusDetails)
                .asValidatedError()
        else
            Validated.ok()
    }
}
