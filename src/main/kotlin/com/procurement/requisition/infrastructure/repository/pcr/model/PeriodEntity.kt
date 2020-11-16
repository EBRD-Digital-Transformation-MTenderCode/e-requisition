package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.Period
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class PeriodEntity(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String?
)

fun Period.mappingToEntity() = PeriodEntity(
    startDate = startDate?.asString(),
    endDate = endDate?.asString()
)

fun PeriodEntity.mappingToDomain(): Result<Period, JsonErrors> {
    val startDate = startDate?.asLocalDateTime()?.onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate?.asLocalDateTime()?.onFailure { return it.repath(path = "/endDate") }
    return Period(startDate = startDate, endDate = endDate).asSuccess()
}
