package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.isDataTypeMatched

data class MinValue(val value: DynamicValue)

val MinValue?.isPresent
    get() = this != null

val MinValue?.isNotPresent
    get() = !isPresent

fun MinValue.isDataTypeMatched(dataType: DynamicValue.DataType) = value.isDataTypeMatched(dataType)
