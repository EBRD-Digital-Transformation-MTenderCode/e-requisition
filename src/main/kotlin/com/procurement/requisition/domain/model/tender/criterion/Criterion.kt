package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.requirement.RequirementGroups

data class Criterion(
    override val id: CriterionId,
    val title: String,
    val source: CriterionSource,
    val description: String?,
    val requirementGroups: RequirementGroups,
    val relatesTo: CriterionRelatesTo?,
    val relatedItem: String?
) : EntityBase<CriterionId>()
