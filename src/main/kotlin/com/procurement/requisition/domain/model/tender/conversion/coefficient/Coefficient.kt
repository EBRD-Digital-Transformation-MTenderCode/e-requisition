package com.procurement.requisition.domain.model.tender.conversion.coefficient

import com.procurement.requisition.domain.model.EntityBase

data class Coefficient(
    override val id: CoefficientId,
    val value: CoefficientValue,
    val coefficient: CoefficientRate
) : EntityBase<CoefficientId>()
