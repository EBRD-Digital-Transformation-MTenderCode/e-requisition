package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.requirement.RequirementGroup
import com.procurement.requisition.domain.model.requirement.Requirements
import com.procurement.requisition.infrastructure.handler.converter.asRequirementGroupId
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class RequirementGroupEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<RequirementEntity>
)

fun RequirementGroup.serialization() = RequirementGroupEntity(
    id = id.underlying,
    description = description,
    requirements = requirements.map { it.serialization() },
)

fun RequirementGroupEntity.deserialization(): Result<RequirementGroup, JsonErrors> {
    val id = id.asRequirementGroupId().onFailure { return it.repath(path = "/id") }
    val requirements = requirements
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "requirements")) }
        .mapIndexed { idx, entity ->
            entity.deserialization().onFailure { return it.repath(path = "/requirements[$idx]") }
        }
        .let { Requirements(it) }

    return RequirementGroup(
        id = id,
        description = description,
        requirements = requirements,
    ).asSuccess()
}
