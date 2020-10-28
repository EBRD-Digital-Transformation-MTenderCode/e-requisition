package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Period
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class PeriodEntity(
    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String,
    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String
)

fun Period.mappingToEntity() = PeriodEntity(
    startDate = startDate.asString(),
    endDate = endDate.asString()
)

fun PeriodEntity.mappingToDomain(path: String): Result<Period, JsonErrors> {

    val startDate = endDate.asLocalDateTime(path = "$path/startDate")
        .onFailure { return it }

    val endDate = endDate.asLocalDateTime(path = "$path/endDate")
        .onFailure { return it }

    return Period(startDate = startDate, endDate = endDate).asSuccess()
}
