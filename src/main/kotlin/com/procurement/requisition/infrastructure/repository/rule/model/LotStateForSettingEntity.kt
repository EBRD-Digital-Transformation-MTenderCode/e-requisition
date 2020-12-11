package com.procurement.requisition.infrastructure.repository.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.repository.rule.model.LotStateForSettingsRule
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

class LotStateForSettingEntity(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String?,
)

fun LotStateForSettingEntity.convert(): Result<LotStateForSettingsRule, JsonErrors> {
    val status = status.asEnum(target = LotStatus)
        .onFailure { return it.repath(path = "/status") }
    val statusDetails = statusDetails?.asEnum(target = LotStatusDetails)
        ?.onFailure { return it.repath(path = "/statusDetails") }
    return LotStateForSettingsRule(status = status, statusDetails = statusDetails).asSuccess()
}
