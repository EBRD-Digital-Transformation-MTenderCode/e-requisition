package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.application.repository.rule.deserializer.TenderStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.application.repository.rule.model.contains
import com.procurement.requisition.application.service.validate.error.CheckTenderStateErrors
import com.procurement.requisition.application.service.validate.model.CheckTenderStateCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckTenderStateService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val rulesRepository: RulesRepository,
    private val renderStatesRuleDeserializer: TenderStatesRuleDeserializer,
) {

    fun check(command: CheckTenderStateCommand): Validated<Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it.reason.asValidatedError() }
            ?: return CheckTenderStateErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        val tender = pcr.tender

        val tenderStatesRules = tenderStatesRules(command)
            .flatMap { json ->
                renderStatesRuleDeserializer.deserialize(json)
            }
            .onFailure { return it.reason.asValidatedError() }

        val state = TenderStatesRule.State(status = tender.status, statusDetails = tender.statusDetails)
        return if (state !in tenderStatesRules)
            CheckTenderStateErrors.Tender.InvalidState(status = tender.status, statusDetails = tender.statusDetails)
                .asValidatedError()
        else
            Validated.ok()
    }

    fun tenderStatesRules(command: CheckTenderStateCommand): Result<String, Failure> = rulesRepository
        .get(
            country = command.country,
            pmd = command.pmd,
            operationType = command.operationType,
            parameter = RulesRepository.validTenderStates
        )
}
