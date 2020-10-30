package com.procurement.requisition.infrastructure.repository.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

class TenderStatesEntity(states: List<TenderStateEntity>) : List<TenderStatesEntity.TenderStateEntity> by states {

    class TenderStateEntity(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
    )
}

fun TenderStatesEntity.convert(): Result<TenderStatesRule, JsonErrors> =
    failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "/")) }
        .mapIndexedOrEmpty { idx, state ->
            state.convert().onFailure { return it.repath(path = "/[$idx]") }
        }
        .let { TenderStatesRule(it).asSuccess() }

fun TenderStatesEntity.TenderStateEntity.convert(): Result<TenderStatesRule.State, JsonErrors> {
    val status = status.asEnum(target = TenderStatus).onFailure { return it.repath(path = "/status") }
    val statusDetails = statusDetails.asEnum(target = TenderStatusDetails)
        .onFailure { return it.repath(path = "/statusDetails") }

    return TenderStatesRule.State(
        status = status,
        statusDetails = statusDetails
    ).asSuccess()
}
