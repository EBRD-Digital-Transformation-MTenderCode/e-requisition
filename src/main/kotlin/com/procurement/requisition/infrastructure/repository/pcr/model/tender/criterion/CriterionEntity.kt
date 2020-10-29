package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.requirement.RequirementGroups
import com.procurement.requisition.domain.model.tender.criterion.Criterion
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.infrastructure.handler.converter.asCriterionId
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class CriterionEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
    @field:JsonProperty("source") @param:JsonProperty("source") val source: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
    @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroupEntity>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
)

fun Criterion.serialization() = CriterionEntity(
    id = id.underlying,
    title = title,
    source = source.asString(),
    description = description,
    relatesTo = relatesTo?.asString(),
    relatedItem = relatedItem,
    requirementGroups = requirementGroups.map { it.serialization() },
)

fun CriterionEntity.deserialization(path: String): Result<Criterion, JsonErrors> {
    val id = id.asCriterionId(path = "$path/id").onFailure { return it }
    val source = source.asEnum(target = CriterionSource, path = "$path/source")
        .onFailure { return it }
    val relatesTo = relatesTo?.asEnum(target = CriterionRelatesTo, path = "$path/relatesTo")
        ?.onFailure { return it }
    val requirementGroups = requirementGroups
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/requirementGroups")) }
        .mapIndexedOrEmpty { idx, requirementGroup ->
            requirementGroup.deserialization(path = "$path/requirementGroups[$idx]").onFailure { return it }
        }
        .let { RequirementGroups(it) }

    return Criterion(
        id = id,
        title = title,
        description = description,
        source = source,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        requirementGroups = requirementGroups,
    ).asSuccess()
}
