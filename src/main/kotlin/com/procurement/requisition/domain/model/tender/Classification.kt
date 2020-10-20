package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.infrastructure.bind.classification.ClassificationId
import com.procurement.requisition.infrastructure.bind.classification.ClassificationScheme

data class Classification(
    override val id: ClassificationId,
    val scheme: ClassificationScheme,
) : EntityBase<ClassificationId>()

fun Classification.equals(other: Classification, n: Int): Boolean {
    if (scheme != other.scheme) return false
    if (id.length != other.id.length) return false
    return id.startsWith(prefix = other.id.substring(0, n), ignoreCase = true)
}
