package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.extension.format
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class RequirementEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonProperty("dataType") @param:JsonProperty("dataType") val dataType: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("expectedValue") @param:JsonProperty("expectedValue") val expectedValue: DynamicValue?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("minValue") @param:JsonProperty("minValue") val minValue: DynamicValue?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maxValue") @param:JsonProperty("maxValue") val maxValue: DynamicValue?,
) {

    data class Period(
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: String,
        @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String
    )
}

fun Requirement.serialization() = RequirementEntity(
    id = id,
    title = title,
    description = description,
    period = period?.let {
        RequirementEntity.Period(startDate = it.startDate.format(), endDate = it.endDate.format())
    },
    dataType = dataType.asString(),
    expectedValue = expectedValue?.value,
    minValue = minValue?.value,
    maxValue = maxValue?.value
)

fun RequirementEntity.deserialization(): Result<Requirement, JsonErrors> {
    val period = period?.deserialization()?.onFailure { return it.repath(path = "/period") }

    val dataType = dataType.asEnum(target = DynamicValue.DataType)
        .onFailure { return it.repath(path = "/dataType") }

    return Requirement(
        id = id,
        title = title,
        description = description,
        period = period,
        dataType = dataType,
        expectedValue = expectedValue?.let { ExpectedValue(it) },
        minValue = minValue?.let { MinValue(it) },
        maxValue = maxValue?.let { MaxValue(it) }
    ).asSuccess()
}

fun RequirementEntity.Period.deserialization(): Result<Requirement.Period, JsonErrors> {
    val startDate = startDate.asLocalDateTime().onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate.asLocalDateTime().onFailure { return it.repath(path = "/endDate") }
    return Requirement.Period(startDate = startDate, endDate = endDate).asSuccess()
}
