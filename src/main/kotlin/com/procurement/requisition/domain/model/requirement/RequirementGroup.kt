package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.domain.model.EntityBase

data class RequirementGroup(
    override val id: RequirementGroupId,
    val description: String?,
    val requirements: Requirements
) : EntityBase<RequirementGroupId>()
