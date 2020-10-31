package com.procurement.requisition.infrastructure.repository.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.repository.rule.model.LotStatesRule
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

class LotStatusesEntity(states: List<LotStatusEntity>) : List<LotStatusesEntity.LotStatusEntity> by states {

    class LotStatusEntity(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    )
}

fun LotStatusesEntity.convert(): Result<LotStatesRule, JsonErrors> =
    failureIfEmpty { return Result.failure(JsonErrors.EmptyArray()) }
        .mapIndexedOrEmpty { idx, state ->
            state.convert().onFailure { return it.repath(path = "/[$idx]") }
        }
        .let { LotStatesRule(it).asSuccess() }

fun LotStatusesEntity.LotStatusEntity.convert(): Result<LotStatesRule.State, JsonErrors> {
    val status = status.asEnum(target = LotStatus).onFailure { return it.repath(path = "/status") }
    return LotStatesRule.State(status = status).asSuccess()
}
