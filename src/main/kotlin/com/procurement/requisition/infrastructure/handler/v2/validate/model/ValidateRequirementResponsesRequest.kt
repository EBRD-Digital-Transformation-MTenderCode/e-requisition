package com.procurement.requisition.infrastructure.handler.v2.validate.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.model.requirement.RequirementRsValue

data class ValidateRequirementResponsesRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender?,

    @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: Bids,
) {

    data class Tender(
        @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String>,
    )

    data class Bids(
        @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
    ) {

        data class Detail(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("requirementResponses") @param:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?,
        ) {

            data class Item(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            )

            data class RequirementResponse(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementRsValue,
                @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("period") @param:JsonProperty("period") val period: Period?,
            ) {

                data class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                )

                data class Period(
                    @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: String,
                    @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String,
                )
            }
        }
    }
}
