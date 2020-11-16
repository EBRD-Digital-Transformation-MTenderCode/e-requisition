package com.procurement.requisition.application.service.create.pcr.model

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.classification.ClassificationId
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessScheme
import com.procurement.requisition.domain.model.relatedprocesses.Relationship
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.conversion.ConversionId
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientId
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.target.TargetId
import com.procurement.requisition.domain.model.tender.target.TargetRelatedItem
import com.procurement.requisition.domain.model.tender.target.observation.ObservationId
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.domain.model.tender.unit.UnitId
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreatePCRResult(
    val ocid: Ocid,
    val token: Token,
    val tender: Tender,
    val relatedProcesses: List<RelatedProcess>
) {

    data class Tender(
        val id: TenderId,
        val status: TenderStatus,
        val statusDetails: TenderStatusDetails,
        val date: LocalDateTime,
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
        val electronicAuctions: ElectronicAuctions?,
        val documents: List<Document>,
        val value: Value
    ) {

        data class Lot(
            val id: LotId,
            val internalId: String?,
            val title: String,
            val description: String?,
            val status: LotStatus,
            val classification: Classification,
            val variants: List<Variant>
        ) {

            data class Variant(
                val hasVariants: Boolean,
                val variantsDetails: String?
            )
        }

        data class Item(
            val id: ItemId,
            val internalId: String?,
            val description: String,
            val quantity: BigDecimal,
            val classification: Classification,
            val unit: Unit,
            val relatedLot: LotId
        )

        data class Target(
            val id: TargetId,
            val title: String,
            val relatesTo: TargetRelatesTo,
            val relatedItem: TargetRelatedItem,
            val observations: List<Observation>
        ) {

            data class Observation(
                val id: ObservationId,
                val period: Period?,
                val measure: ObservationMeasure,
                val unit: Unit,
                val dimensions: Dimensions?,
                val notes: String,
                val relatedRequirementId: RequirementId?
            ) {

                data class Period(
                    val endDate: LocalDateTime?,
                    val startDate: LocalDateTime?
                )

                data class Dimensions(
                    val requirementClassIdPR: String
                )
            }
        }

        data class Criterion(
            val id: CriterionId,
            val title: String,
            val source: CriterionSource,
            val description: String?,
            val requirementGroups: List<RequirementGroup>,
            val relatesTo: CriterionRelatesTo?,
            val relatedItem: String?
        ) {

            data class RequirementGroup(
                val id: RequirementGroupId,
                val description: String?,
                val requirements: List<Requirement>
            )
        }

        data class Conversion(
            val id: ConversionId,
            val relatesTo: ConversionRelatesTo,
            val relatedItem: String,
            val rationale: String,
            val description: String?,
            val coefficients: List<Coefficient>
        ) {

            data class Coefficient(
                val id: CoefficientId,
                val value: DynamicValue,
                val coefficient: CoefficientRate
            )
        }

        data class Document(
            val id: DocumentId,
            val documentType: DocumentType,
            val title: String,
            val description: String?,
            val relatedLots: List<LotId>
        )

        data class Value(
            val currency: String
        )

        data class ElectronicAuctions(
            val details: List<Detail>
        ) {
            data class Detail(
                val id: String,
                val relatedLot: LotId,
            )
        }
    }

    data class Classification(
        val id: ClassificationId,
        val scheme: ClassificationScheme,
        val description: String,
        val uri: String? = null
    )

    data class Unit(val id: UnitId, val name: String)

    data class RelatedProcess(
        val id: RelatedProcessId,
        val scheme: RelatedProcessScheme,
        val identifier: String,
        val relationship: List<Relationship>,
        val uri: String
    )
}
