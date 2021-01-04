package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource

data class CreatedRequestsForEvPanelsResult(
    val criteria: Criterion
) {
    data class Criterion(
        val id: CriterionId,
        val title: String,
        val source: CriterionSource,
        val relatesTo: CriterionRelatesTo,
        val description: String?,
        val requirementGroups: List<RequirementGroup>
    ) {
        data class RequirementGroup(
            val id: RequirementGroupId,
            val requirements: List<Requirement>
        )
    }
}
