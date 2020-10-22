package com.procurement.requisition.domain.model.tender.target

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.target.observation.Observations

data class Target(
    override val id: TargetId,
    val title: String,
    val relatesTo: TargetRelatesTo,
    val relatedItem: TargetRelatedItem,
    val observations: Observations
) : EntityBase<TargetId>()
