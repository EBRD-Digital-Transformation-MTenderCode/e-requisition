package com.procurement.requisition.infrastructure.handler.pcr.validate

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionId
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientId
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.target.TargetId
import com.procurement.requisition.domain.model.tender.target.TargetRelatedItem
import com.procurement.requisition.domain.model.tender.target.observation.ObservationId
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.domain.model.tender.unit.UnitId
import com.procurement.requisition.infrastructure.bind.classification.ClassificationId
import com.procurement.requisition.infrastructure.bind.classification.ClassificationScheme
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateDeserializer
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateSerializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantityDeserializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantitySerializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementsDeserializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementsSerializer
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ValidatePCRDataParams(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(

        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

        @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>? = null,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<Target>? = null,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<Criterion>? = null,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<Conversion>? = null,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>? = null,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>? = null

    ) {

        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,
            @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: Variant
        ) {

            data class Variant(
                @field:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("variantsDetails") @param:JsonProperty("variantsDetails") val variantsDetails: String?
            )
        }

        data class Item(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ItemId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @JsonDeserialize(using = QuantityDeserializer::class)
            @JsonSerialize(using = QuantitySerializer::class)
            @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,

            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: LotId
        )

        data class Target(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: TargetId,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: TargetRelatesTo,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: TargetRelatedItem,

            @field:JsonProperty("observations") @param:JsonProperty("observations") val observations: List<Observation>
        ) {
            data class Observation(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: ObservationId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?,

                @param:JsonProperty("measure") @field:JsonProperty("measure") val measure: ObservationMeasure,
                @param:JsonProperty("unit") @field:JsonProperty("unit") val unit: Unit,
                @param:JsonProperty("dimensions") @field:JsonProperty("dimensions") val dimensions: Dimensions,
                @param:JsonProperty("notes") @field:JsonProperty("notes") val notes: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("relatedRequirementId") @field:JsonProperty("relatedRequirementId") val relatedRequirementId: RequirementId?
            ) {

                data class Period(
                    @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime,
                    @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                )

                data class Dimensions(
                    @param:JsonProperty("requirementClassIdPR") @field:JsonProperty("requirementClassIdPR") val requirementClassIdPR: String
                )
            }
        }

        data class Criterion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: CriterionId,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("source") @param:JsonProperty("source") val source: CriterionSource? = null,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
            @field:JsonProperty("requirementGroups") @param:JsonProperty("requirementGroups") val requirementGroups: List<RequirementGroup>,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: CriterionRelatesTo?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String?
        ) {
            data class RequirementGroup(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementGroupId,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                @JsonDeserialize(using = RequirementsDeserializer::class)
                @JsonSerialize(using = RequirementsSerializer::class)
                @field:JsonProperty("requirements") @param:JsonProperty("requirements") val requirements: List<Requirement>
            )
        }

        data class Conversion(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: ConversionId,
            @field:JsonProperty("relatesTo") @param:JsonProperty("relatesTo") val relatesTo: ConversionRelatesTo,
            @field:JsonProperty("relatedItem") @param:JsonProperty("relatedItem") val relatedItem: String,
            @field:JsonProperty("rationale") @param:JsonProperty("rationale") val rationale: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,
            @field:JsonProperty("coefficients") @param:JsonProperty("coefficients") val coefficients: List<Coefficient>
        ) {
            data class Coefficient(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: CoefficientId,
                @field:JsonProperty("value") @param:JsonProperty("value") val value: CoefficientValue,

                @JsonDeserialize(using = CoefficientRateDeserializer::class)
                @JsonSerialize(using = CoefficientRateSerializer::class)
                @field:JsonProperty("coefficient") @param:JsonProperty("coefficient") val coefficient: CoefficientRate
            )
        }

        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,

            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>? = null
        )
    }

    data class Classification(
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: ClassificationScheme,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: ClassificationId,
    )

    data class Unit(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UnitId
    )
}