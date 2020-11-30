package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.RequirementEntity

data class CreateRequestsForEvPanelsResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: Criterion
) {
    data class Criterion(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
        @field:JsonProperty("source") @param:JsonProperty("source") val source: String,
        @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
        @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>
    ) {
        data class RequirementGroup(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<RequirementEntity>
        )
    }
}
