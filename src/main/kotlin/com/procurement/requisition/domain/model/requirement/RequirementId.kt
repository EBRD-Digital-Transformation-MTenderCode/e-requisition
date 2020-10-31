package com.procurement.requisition.domain.model.requirement

import java.util.*

typealias RequirementId = String

fun generateRequirementId(): RequirementId = UUID.randomUUID().toString()
