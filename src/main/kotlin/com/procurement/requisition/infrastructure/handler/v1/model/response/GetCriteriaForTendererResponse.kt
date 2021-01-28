package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.model.result.GetCriteriaForTendererResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.RequirementEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.serialization

data class GetCriteriaForTendererResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>
) {
    data class Criterion(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("source") @param:JsonProperty("source") val source: String?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
        @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
    ) {

        data class Classification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
        )

        data class RequirementGroup(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<RequirementEntity>
        )
    }

    companion object {

        fun fromResult(result: GetCriteriaForTendererResult): GetCriteriaForTendererResponse =
            GetCriteriaForTendererResponse(
                criteria = result.criteria.map { it.convert() }
            )

        fun GetCriteriaForTendererResult.Criterion.convert(): Criterion =
            Criterion(
                id = this.id.underlying,
                title = this.title,
                description = this.description,
                source = this.source?.asString(),
                relatesTo = this.relatesTo?.asString(),
                relatedItem = this.relatedItem,
                classification = this.classification?.convert(),
                requirementGroups = this.requirementGroups.map { it.convert() }
            )

        fun GetCriteriaForTendererResult.Criterion.Classification.convert(): Criterion.Classification =
            Criterion.Classification(
                id = this.id,
                scheme = this.scheme.asString()
            )

        fun GetCriteriaForTendererResult.Criterion.RequirementGroup.convert(): Criterion.RequirementGroup =
            Criterion.RequirementGroup(
                id = this.id.underlying,
                description = this.description,
                requirements = this.requirements.map { it.serialization() }
            )

    }
}
