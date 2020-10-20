package com.procurement.requisition.application.service.validate

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.tender.Classification
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
import java.math.BigDecimal
import java.time.LocalDateTime

data class ValidationPCRData(
    val tender: Tender
) {

    data class Tender(
        val title: String,
        val description: String,
        val classification: Classification,
        val lots: List<Lot>,
        val items: List<Item>,
        val targets: List<Target>,
        val criteria: List<Criterion>,
        val conversions: List<Conversion>,
        val procurementMethodModalities: List<ProcurementMethodModality>,
        val awardCriteria: AwardCriteria?,
        val awardCriteriaDetails: AwardCriteriaDetails?,
        val documents: List<Document>
    ) {

        data class Lot(
            override val id: LotId,
            val internalId: String?,
            val title: String,
            val description: String?,
            val classification: Classification,
            val variants: Variant
        ) : EntityBase<LotId>() {

            data class Variant(
                val hasVariants: Boolean,
                val variantsDetails: String?
            )
        }

        data class Item(
            override val id: ItemId,
            val internalId: String?,
            val description: String,
            val quantity: BigDecimal,
            val classification: Classification,
            val unit: Unit,
            val relatedLot: LotId
        ) : EntityBase<ItemId>()

        data class Target(
            override val id: TargetId,
            val title: String,
            val relatesTo: TargetRelatesTo,
            val relatedItem: TargetRelatedItem,
            val observations: List<Observation>
        ) : EntityBase<TargetId>() {

            data class Observation(
                override val id: ObservationId,
                val period: Period?,
                val measure: ObservationMeasure,
                val unit: Unit,
                val dimensions: Dimensions,
                val notes: String,
                val relatedRequirementId: RequirementId?
            ) : EntityBase<ObservationId>() {

                data class Period(
                    val endDate: LocalDateTime,
                    val startDate: LocalDateTime
                )

                data class Dimensions(
                    val requirementClassIdPR: String
                )
            }
        }

        data class Criterion(
            override val id: CriterionId,
            val title: String,
            val source: CriterionSource?,
            val description: String?,
            val requirementGroups: List<RequirementGroup>,
            val relatesTo: CriterionRelatesTo?,
            val relatedItem: String?
        ) : EntityBase<CriterionId>() {

            data class RequirementGroup(
                override val id: RequirementGroupId,
                val description: String?,
                val requirements: List<Requirement>
            ) : EntityBase<RequirementGroupId>()
        }

        data class Conversion(
            override val id: ConversionId,
            val relatesTo: ConversionRelatesTo,
            val relatedItem: String,
            val rationale: String,
            val description: String?,
            val coefficients: List<Coefficient>
        ) : EntityBase<CriterionId>() {

            data class Coefficient(
                override val id: CoefficientId,
                val value: CoefficientValue,
                val coefficient: CoefficientRate
            ) : EntityBase<CriterionId>()
        }

        data class Document(
            override val id: DocumentId,
            val documentType: DocumentType,
            val title: String,
            val description: String?,
            val relatedLots: List<LotId>
        ) : EntityBase<DocumentId>()
    }

    data class Unit(
        override val id: UnitId
    ) : EntityBase<UnitId>()
}
