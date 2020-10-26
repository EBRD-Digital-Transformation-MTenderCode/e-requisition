package com.procurement.requisition.application.service.create.pcr.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatedItem
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.target.TargetRelatedItem
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreatePCRCommand(
    val cpid: Cpid,
    val date: LocalDateTime,
    val stateFE: StateFE,
    val owner: String,
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
        val awardCriteria: AwardCriteria,
        val awardCriteriaDetails: AwardCriteriaDetails,
        val documents: List<Document>,
        val value: Value
    ) {

        data class Lot(
            override val id: String,
            val internalId: String?,
            val title: String,
            val description: String?,
            val classification: Classification,
            val variants: Variant
        ) : EntityBase<String>() {

            data class Variant(
                val hasVariants: Boolean,
                val variantsDetails: String?
            )
        }

        data class Item(
            override val id: String,
            val internalId: String?,
            val description: String,
            val quantity: BigDecimal,
            val classification: Classification,
            val unit: Unit,
            val relatedLot: String
        ) : EntityBase<String>()

        data class Target(
            override val id: String,
            val title: String,
            val relatesTo: TargetRelatesTo,
            val relatedItem: TargetRelatedItem,
            val observations: List<Observation>
        ) : EntityBase<String>() {

            data class Observation(
                override val id: String,
                val period: Period?,
                val measure: ObservationMeasure,
                val unit: Unit,
                val dimensions: Dimensions,
                val notes: String,
                val relatedRequirementId: String?
            ) : EntityBase<String>() {

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
            override val id: String,
            val title: String,
            val description: String?,
            val requirementGroups: List<RequirementGroup>,
            val relatesTo: CriterionRelatesTo?,
            val relatedItem: CriterionRelatedItem?
        ) : EntityBase<String>() {

            data class RequirementGroup(
                override val id: String,
                val description: String?,
                val requirements: List<Requirement>
            ) : EntityBase<String>()
        }

        data class Conversion(
            override val id: String,
            val relatesTo: ConversionRelatesTo,
            val relatedItem: String,
            val rationale: String,
            val description: String?,
            val coefficients: List<Coefficient>
        ) : EntityBase<String>() {

            data class Coefficient(
                override val id: String,
                val value: CoefficientValue,
                val coefficient: CoefficientRate
            ) : EntityBase<String>()
        }

        data class Document(
            override val id: DocumentId,
            val documentType: DocumentType,
            val title: String,
            val description: String?,
            val relatedLots: List<String>
        ) : EntityBase<DocumentId>()

        data class Value(
            val currency: String
        )
    }

    data class Unit(
        override val id: String,
        val name: String
    ) : EntityBase<String>()
}
