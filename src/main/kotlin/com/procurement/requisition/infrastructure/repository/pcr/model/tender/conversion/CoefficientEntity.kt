package com.procurement.requisition.infrastructure.repository.pcr.model.tender.conversion

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.conversion.coefficient.Coefficient
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateDeserializer
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateSerializer
import com.procurement.requisition.infrastructure.handler.converter.asCoefficientId
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class CoefficientEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue,

    @JsonDeserialize(using = CoefficientRateDeserializer::class)
    @JsonSerialize(using = CoefficientRateSerializer::class)
    @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
)

fun Coefficient.serialization() =
    CoefficientEntity(id = id.underlying, value = value, coefficient = coefficient)

fun CoefficientEntity.deserialization(path: String): Result<Coefficient, JsonErrors> {
    val id = id.asCoefficientId(path = "$path/id").onFailure { return it }
    return Coefficient(id = id, value = value, coefficient = coefficient).asSuccess()
}
