package com.procurement.requisition.infrastructure.repository.pcr.model.tender.conversion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.conversion.Conversion
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.Coefficients
import com.procurement.requisition.infrastructure.handler.converter.asConversionId
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class ConversionEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
    @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<CoefficientEntity>
)

fun Conversion.serialization() = ConversionEntity(
    id = id.underlying,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    rationale = rationale,
    description = description,
    coefficients = coefficients.map { it.serialization() }
)

fun ConversionEntity.deserialization(path: String): Result<Conversion, JsonErrors> {
    val id = id.asConversionId(path = "$path/id").onFailure { return it }
    val relatesTo = relatesTo.asEnum(target = ConversionRelatesTo, path = "$path/relatesTo")
        .onFailure { return it }
    val coefficients = coefficients
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/coefficients")) }
        .mapIndexedOrEmpty { idx, coefficient ->
            coefficient.deserialization(path = "$path/coefficients[$idx]").onFailure { return it }
        }
        .let { Coefficients(it) }

    return Conversion(
        id = id,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        rationale = rationale,
        description = description,
        coefficients = coefficients
    ).asSuccess()
}
