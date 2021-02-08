package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.classification.ClassificationId

data class CriterionClassification(
    override val id: ClassificationId,
    val scheme: String
) : EntityBase<ClassificationId>()
