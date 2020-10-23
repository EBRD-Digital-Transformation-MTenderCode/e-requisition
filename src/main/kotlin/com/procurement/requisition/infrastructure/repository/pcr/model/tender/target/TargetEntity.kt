package com.procurement.requisition.infrastructure.repository.pcr.model.tender.target

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.target.Target
import com.procurement.requisition.domain.model.tender.target.TargetId
import com.procurement.requisition.domain.model.tender.target.observation.Observations
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.observation.ObservationEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.observation.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.observation.serialization
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class TargetEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
    @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<ObservationEntity>
)

fun Target.serialization() = TargetEntity(
    id = id.underlying,
    title = title,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    observations = observations.map { it.serialization() }
)

fun TargetEntity.deserialization(path: String): Result<Target, JsonErrors> {
    val id = TargetId.orNull(id)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = TargetId.pattern,
                reason = null
            )
        )
    val relatesTo = relatesTo.asEnum(target = TargetRelatesTo, path = "$path/relatesTo")
        .onFailure { return it }
    val observations = observations
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/observations")) }
        .mapIndexedOrEmpty { observationIdx, observation ->
            observation.deserialization("$path/observations[$observationIdx]").onFailure { return it }
        }
        .let { Observations(it) }

    return Target(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        observations = observations
    ).asSuccess()
}
