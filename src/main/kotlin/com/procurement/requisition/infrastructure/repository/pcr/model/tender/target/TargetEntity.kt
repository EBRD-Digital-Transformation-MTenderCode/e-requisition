package com.procurement.requisition.infrastructure.repository.pcr.model.tender.target

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.target.Target
import com.procurement.requisition.domain.model.tender.target.observation.Observations
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.converter.asTargetId
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

fun TargetEntity.deserialization(): Result<Target, JsonErrors> {
    val id = id.asTargetId().onFailure { return it.repath(path = "/id") }
    val relatesTo = relatesTo.asEnum(target = TargetRelatesTo)
        .onFailure { return it.repath(path = "/relatesTo") }
    val observations = observations
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath("/observations")) }
        .mapIndexedOrEmpty { observationIdx, observation ->
            observation.deserialization().onFailure { return it.repath(path = "/observations[$observationIdx]") }
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
