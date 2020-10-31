package com.procurement.requisition.domain.model.tender.conversion

import com.procurement.requisition.domain.model.EntityBase
import com.procurement.requisition.domain.model.tender.conversion.coefficient.Coefficients

data class Conversion(
    override val id: ConversionId,
    val relatesTo: ConversionRelatesTo,
    val relatedItem: String,
    val rationale: String,
    val description: String?,
    val coefficients: Coefficients
) : EntityBase<ConversionId>()
