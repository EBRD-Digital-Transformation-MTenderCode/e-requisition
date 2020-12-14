package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class FindCriteriaAndTargetsForPacsResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<Target>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>
    ) {

        data class Target(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<Observation>
        ) {
            data class Observation(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String?
            ) {

                data class Unit(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                )
            }
        }

        data class Criterion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
        ) {

            data class RequirementGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
            ){

                data class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String
                )
            }
        }
    }
}
