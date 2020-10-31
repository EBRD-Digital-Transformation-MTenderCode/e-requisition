package com.procurement.requisition.domain.model.tender.target.observation

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.Period
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.tender.unit.Unit

data class Observation(
    override val id: ObservationId,
    val period: Period?,
    val measure: ObservationMeasure,
    val unit: Unit,
    val dimensions: Dimensions,
    val notes: String,
    val relatedRequirementId: RequirementId?
) : EntityBase<ObservationId>()
