package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroup
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatedItem
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.domain.model.tender.criterion.Criterion as DomainCriterion
import com.procurement.requisition.domain.model.tender.criterion.CriterionClassification as DomainClassification

data class GetCriteriaForTendererResult(
    val criteria: List<Criterion>
) {
    data class Criterion(
        val id: CriterionId,
        val title: String,
        val classification: Classification?,
        val source: CriterionSource?,
        val description: String?,
        val requirementGroups: List<RequirementGroup>,
        val relatesTo: CriterionRelatesTo?,
        val relatedItem: CriterionRelatedItem?
    ) {

        data class Classification(
            val id: String,
            val scheme: String
        )

        data class RequirementGroup(
            val id: RequirementGroupId,
            val description: String?,
            val requirements: List<Requirement>
        )
    }

    companion object {
        fun fromDomain(criterion: DomainCriterion): Criterion =
            Criterion(
                id = criterion.id,
                title = criterion.title,
                description = criterion.description,
                relatedItem = criterion.relatedItem,
                relatesTo = criterion.relatesTo,
                source = criterion.source,
                classification = criterion.classification?.let { fromDomain(it) },
                requirementGroups = criterion.requirementGroups.map { fromDomain(it) }
            )

        private fun fromDomain(classification: DomainClassification): Criterion.Classification =
            Criterion.Classification(
                id = classification.id,
                scheme = classification.scheme
            )

        private fun fromDomain(requirementGroup: RequirementGroup): Criterion.RequirementGroup =
            Criterion.RequirementGroup(
                id = requirementGroup.id,
                description = requirementGroup.description,
                requirements = requirementGroup.requirements
            )
    }
}
