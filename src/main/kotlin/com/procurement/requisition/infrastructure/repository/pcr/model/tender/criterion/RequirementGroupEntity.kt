package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroup
import com.procurement.requisition.domain.model.requirement.Requirements
import com.procurement.requisition.infrastructure.bind.requirement.RequirementsDeserializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementsSerializer
import com.procurement.requisition.infrastructure.handler.converter.asRequirementGroupId
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class RequirementGroupEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @JsonDeserialize(using = RequirementsDeserializer::class)
    @JsonSerialize(using = RequirementsSerializer::class)
    @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
)

fun RequirementGroup.serialization() = RequirementGroupEntity(
    id = id.underlying,
    description = description,
    requirements = requirements.toList(),
)

fun RequirementGroupEntity.deserialization(): Result<RequirementGroup, JsonErrors> {
    val id = id.asRequirementGroupId().onFailure { return it.repath(path = "/id") }
    return RequirementGroup(
        id = id,
        description = description,
        requirements = Requirements(requirements.toList()),
    ).asSuccess()
}
