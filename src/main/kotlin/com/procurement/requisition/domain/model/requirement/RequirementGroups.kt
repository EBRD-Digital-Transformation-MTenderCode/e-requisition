package com.procurement.requisition.domain.model.requirement

class RequirementGroups(values: List<RequirementGroup> = emptyList()) : List<RequirementGroup> by values {

    constructor(requirementGroup: RequirementGroup) : this(listOf(requirementGroup))
}
