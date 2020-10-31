package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.application.repository.rule.deserializer.LotStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.model.LotStatesRule
import com.procurement.requisition.application.repository.rule.model.contains
import com.procurement.requisition.application.service.validate.error.CheckLotsStateErrors
import com.procurement.requisition.application.service.validate.error.CheckTenderStateErrors
import com.procurement.requisition.application.service.validate.model.CheckLotsStateCommand
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import org.springframework.stereotype.Service

@Service
class CheckLotsStateService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val rulesRepository: RulesRepository,
    private val lotStatesRuleDeserializer: LotStatesRuleDeserializer,
) {

    fun check(command: CheckLotsStateCommand): Validated<Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it.reason.asValidatedError() }
            ?: return CheckTenderStateErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asValidatedError()

        val lotStateRules: LotStatesRule = lotStateRules(command)
            .flatMap { json ->
                lotStatesRuleDeserializer.deserialize(json)
            }
            .onFailure { return it.reason.asValidatedError() }

        val tender = pcr.tender
        val lotsById = tender.lots.associateBy { it.id }

        command.tender.lots
            .forEach { lot ->
                lotsById[lot.id]
                    ?.apply {
                        if (status !in lotStateRules)
                            return CheckLotsStateErrors.Lot.InvalidState(lotId = lot.id, status = tender.status)
                                .asValidatedError()
                    }
                    ?: return CheckLotsStateErrors.Lot.Unknown(lotId = lot.id).asValidatedError()
            }

        return Validated.ok()
    }

    fun lotStateRules(command: CheckLotsStateCommand): Result<String, Failure> = rulesRepository.get(
        country = command.country,
        pmd = command.pmd,
        operationType = command.operationType,
        parameter = RulesRepository.validLotStates
    )
}
