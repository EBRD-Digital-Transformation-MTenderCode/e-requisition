package com.procurement.requisition.infrastructure.handler.v1.get.award.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue

data class GetAwardCriteriaAndConversionsResponse(
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>?
) {
    data class Conversion(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
        @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
        @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<Coefficient>
    ) {
        data class Coefficient(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue,
            @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
        )
    }
}
