package com.procurement.requisition.application.service.validate.model

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.infrastructure.bind.classification.ClassificationScheme
import java.math.BigDecimal
import java.time.LocalDateTime

data class ValidatePCRDataCommand(
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
        val documents: List<Document>
    ) {

        data class Lot(
            override val id: String,
            val internalId: String?,
            val title: String,
            val description: String?,
            val classification: Classification,
            val variants: List<Variant>
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
            val relatedItem: String,
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
            val relatedItem: String?
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
            override val id: String,
            val documentType: DocumentType,
            val title: String,
            val description: String?,
            val relatedLots: List<String>
        ) : EntityBase<String>()
    }

    data class Classification(
        override val id: String,
        val scheme: ClassificationScheme,
    ) : EntityBase<String>()

    data class Unit(
        override val id: String
    ) : EntityBase<String>()
}
