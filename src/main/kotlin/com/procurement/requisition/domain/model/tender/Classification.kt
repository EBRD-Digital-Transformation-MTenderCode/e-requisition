package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.classification.ClassificationId
import com.procurement.requisition.domain.model.classification.ClassificationScheme

data class Classification(
    override val id: ClassificationId,
    val scheme: ClassificationScheme,
    val description: String,
    val uri: String? = null
) : EntityBase<ClassificationId>()
