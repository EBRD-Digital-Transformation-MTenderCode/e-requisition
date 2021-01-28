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
        @field:JsonProperty("status") @param:JsonProperty("status") val status: Status,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: StatusDetails?
    ) {
        class Status(
            @field:JsonProperty("value") @param:JsonProperty("value") val value: String,
        )

        class StatusDetails(
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("value") @param:JsonProperty("value") val value: String?
        )
    }
}

fun ValidLotStatesRuleEntity.convert(): Result<ValidLotStatesRule, JsonErrors> =
    failureIfEmpty { return Result.failure(JsonErrors.EmptyArray()) }
        .mapIndexedOrEmpty { idx, state ->
            state.convert().onFailure { return it.repath(path = "/[$idx]") }
        }
        .let { ValidLotStatesRule(it).asSuccess() }

fun ValidLotStatesRuleEntity.LotStatusEntity.convert(): Result<ValidLotStatesRule.State, JsonErrors> {
    val status = status.value
        .asEnum(target = LotStatus)
        .onFailure { return it.repath(path = "/status") }
        .let { ValidLotStatesRule.State.Status(it) }

    val statusDetails = statusDetails
        ?.let { statusDetails ->
            statusDetails.value?.asEnum(target = LotStatusDetails)
                ?.onFailure { return it.repath(path = "/statusDetails") }
                .let { ValidLotStatesRule.State.StatusDetails(it) }
        }

    return ValidLotStatesRule.State(
        status = status,
        statusDetails = statusDetails
    ).asSuccess()
}
