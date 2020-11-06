package com.procurement.requisition.domain.model.tender.conversion.coefficient

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.EntityBase

data class Coefficient(
    override val id: CoefficientId,
    val value: DynamicValue,
    val coefficient: CoefficientRate
) : EntityBase<CoefficientId>()
