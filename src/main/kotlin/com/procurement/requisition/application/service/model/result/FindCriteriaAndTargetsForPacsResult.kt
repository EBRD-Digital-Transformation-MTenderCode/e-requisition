package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatedItem
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.target.TargetId
import com.procurement.requisition.domain.model.tender.target.observation.ObservationId
import com.procurement.requisition.domain.model.tender.unit.UnitId

data class FindCriteriaAndTargetsForPacsResult(
    val tender: Tender
) {

    data class Tender(
        val targets: List<Target>,
        val criteria: List<Criterion>
    ) {

        data class Target(
            val id: TargetId,
            val observations: List<Observation>
        ) {

            data class Observation(
                val id: ObservationId,
                val unit: Unit,
                val relatedRequirementId: String?
            ) {

                data class Unit(
                    val id: UnitId,
                    val name: String
                )
            }
        }

        data class Criterion(
            val id: CriterionId,
            val title: String,
            val requirementGroups: List<RequirementGroup>,
            val relatesTo: CriterionRelatesTo?,
            val relatedItem: CriterionRelatedItem?
        ) {

            data class RequirementGroup(
                val id: RequirementGroupId,
                val requirements: List<Requirement>
            ) {

                data class Requirement(
                    val id: RequirementId,
                    val title: String
                )
            }
        }
    }
}
