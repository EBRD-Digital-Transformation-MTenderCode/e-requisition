package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.isDataTypeMatched

data class MaxValue(val value: DynamicValue)

val MaxValue?.isPresent
    get() = this != null

val MaxValue?.isNotPresent
    get() = !isPresent

fun MaxValue.isDataTypeMatched(dataType: DynamicValue.DataType) = value.isDataTypeMatched(dataType)
