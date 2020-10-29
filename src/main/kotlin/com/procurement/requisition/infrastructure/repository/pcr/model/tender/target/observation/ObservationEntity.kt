package com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.observation

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.target.observation.Observation
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.infrastructure.handler.converter.asObservationId
import com.procurement.requisition.infrastructure.repository.pcr.model.PeriodEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.UnitEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToDomain
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToEntity
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class ObservationEntity(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("period") @field:JsonProperty("period") val period: PeriodEntity?,

    @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: ObservationMeasure,
    @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: UnitEntity,
    @param:JsonProperty("dimensions") @field:JsonProperty("dimensions") val dimensions: DimensionsEntity,
    @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String?
)

fun Observation.serialization() = ObservationEntity(
    id = id.underlying,
    period = period?.mappingToEntity(),
    measure = measure,
    unit = unit.mappingToEntity(),
    dimensions = dimensions.serialization(),
    notes = notes,
    relatedRequirementId = relatedRequirementId,
)

fun ObservationEntity.deserialization(path: String): Result<Observation, JsonErrors> {
    val id = id.asObservationId(path = "$path/id").onFailure { return it }
    val period = period?.mappingToDomain(path = "$path/period")?.onFailure { return it }
    val unit = unit.mappingToDomain(path = "$path/unit").onFailure { return it }
    val dimensions = dimensions.deserialization(path = "$path/dimensions").onFailure { return it }

    return Observation(
        id = id,
        period = period,
        measure = measure,
        unit = unit,
        dimensions = dimensions,
        notes = notes,
        relatedRequirementId = relatedRequirementId,
    ).asSuccess()
}
