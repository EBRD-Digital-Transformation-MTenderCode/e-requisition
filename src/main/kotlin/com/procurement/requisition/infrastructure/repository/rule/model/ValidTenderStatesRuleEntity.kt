package com.procurement.requisition.infrastructure.repository.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.rule.model.ValidTenderStatesRule
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

class ValidTenderStatesRuleEntity(states: List<TenderStateEntity>) : List<ValidTenderStatesRuleEntity.TenderStateEntity> by states {

    class TenderStateEntity(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
    )
}

fun ValidTenderStatesRuleEntity.convert(): Result<ValidTenderStatesRule, JsonErrors> =
    failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "/")) }
        .mapIndexedOrEmpty { idx, state ->
            state.convert().onFailure { return it.repath(path = "/[$idx]") }
        }
        .let { ValidTenderStatesRule(it).asSuccess() }

fun ValidTenderStatesRuleEntity.TenderStateEntity.convert(): Result<ValidTenderStatesRule.State, JsonErrors> {
    val status = status.asEnum(target = TenderStatus).onFailure { return it.repath(path = "/status") }
    val statusDetails = statusDetails.asEnum(target = TenderStatusDetails)
        .onFailure { return it.repath(path = "/statusDetails") }

    return ValidTenderStatesRule.State(
        status = status,
        statusDetails = statusDetails
    ).asSuccess()
}
