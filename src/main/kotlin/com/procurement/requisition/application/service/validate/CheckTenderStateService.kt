package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.application.repository.rule.model.contains
import com.procurement.requisition.application.service.validate.error.CheckTenderStateErrors
import com.procurement.requisition.application.service.validate.model.CheckTenderStateCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckTenderStateService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
    private val rulesRepository: RulesRepository,
) {

    fun check(command: CheckTenderStateCommand): Validated<Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it.reason.asValidatedError() }
            ?: return CheckTenderStateErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        val tender = pcr.tender

        val tenderStatesRules = rulesRepository
            .tenderStates(
                country = command.country,
                pmd = command.pmd,
                operationType = command.operationType
            )
            .onFailure { return it.reason.asValidatedError() }

        return if (tenderStatesRules.contains(status = tender.status, statusDetails = tender.statusDetails))
            Validated.ok()
        else
            CheckTenderStateErrors.InvalidTenderState(status = tender.status, statusDetails = tender.statusDetails)
                .asValidatedError()
    }
}
