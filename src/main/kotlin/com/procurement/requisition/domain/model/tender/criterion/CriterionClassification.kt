package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.classification.ClassificationId
import com.procurement.requisition.domain.model.classification.ClassificationScheme

data class CriterionClassification(
    override val id: ClassificationId,
    val scheme: ClassificationScheme
) : EntityBase<ClassificationId>()
