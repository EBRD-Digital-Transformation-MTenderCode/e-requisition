package com.procurement.requisition.infrastructure.repository.rule.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.rule.model.ValidLotStatesRule
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

class ValidLotStatesRuleEntity(states: List<LotStatusEntity>) : List<ValidLotStatesRuleEntity.LotStatusEntity> by states {

    class LotStatusEntity(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String?
    )
}

fun ValidLotStatesRuleEntity.convert(): Result<ValidLotStatesRule, JsonErrors> =
    failureIfEmpty { return Result.failure(JsonErrors.EmptyArray()) }
        .mapIndexedOrEmpty { idx, state ->
            state.convert().onFailure { return it.repath(path = "/[$idx]") }
        }
        .let { ValidLotStatesRule(it).asSuccess() }

fun ValidLotStatesRuleEntity.LotStatusEntity.convert(): Result<ValidLotStatesRule.State, JsonErrors> {
    val status = status.asEnum(target = LotStatus).onFailure { return it.repath(path = "/status") }
    val statusDetails = statusDetails?.asEnum(target = LotStatusDetails)
        ?.onFailure { return it.repath(path = "/statusDetails") }
    return ValidLotStatesRule.State(status = status, statusDetails = statusDetails).asSuccess()
}
