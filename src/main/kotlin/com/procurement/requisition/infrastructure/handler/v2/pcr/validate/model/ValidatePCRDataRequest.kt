package com.procurement.requisition.infrastructure.handler.v2.pcr.validate.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateDeserializer
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateSerializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantityDeserializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantitySerializer
import java.math.BigDecimal

data class ValidatePCRDataRequest(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(

        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

        @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<Target>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String>?,

        @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: String,
        @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: String,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?

    ) {

        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: List<Variant>
        ) {

            data class Variant(
                @field:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("variantsDetails") @param:JsonProperty("variantsDetails") val variantsDetails: String?
            )
        }

        data class Item(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @JsonDeserialize(using = QuantityDeserializer::class)
            @JsonSerialize(using = QuantitySerializer::class)
            @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,

            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String
        )

        data class Target(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,

            @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<Observation>
        ) {
            data class Observation(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: ObservationMeasure,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("dimensions") @field:JsonProperty("dimensions") val dimensions: Dimensions?,

                @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: String?
            ) {

                data class Period(
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: String?
                )

                data class Dimensions(
                    @param:JsonProperty("requirementClassIdPR") @field:JsonProperty("requirementClassIdPR") val requirementClassIdPR: String
                )
            }
        }

        data class Criterion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
            @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
        ) {
            data class RequirementGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
            ) {

                data class Requirement(
                    @field:JsonProperty("id") @param:JsonProperty("id") override val id: String,
                    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String? = null,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

                    @field:JsonProperty("dataType") @param:JsonProperty("dataType") val dataType: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("expectedValue") @param:JsonProperty("expectedValue") val expectedValue: DynamicValue? = null,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("minValue") @param:JsonProperty("minValue") val minValue: DynamicValue? = null,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("maxValue") @param:JsonProperty("maxValue") val maxValue: DynamicValue? = null,
                ) : EntityBase<String>() {

                    data class Period(
                        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: String,
                        @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String
                    )
                }
            }
        }

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
                @field:JsonProperty("value") @param:JsonProperty("value") val value: DynamicValue,

                @JsonDeserialize(using = CoefficientRateDeserializer::class)
                @JsonSerialize(using = CoefficientRateSerializer::class)
                @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
            )
        }

        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String,

            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>?
        )
    }

    data class Classification(
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    )

    data class Unit(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )
}
