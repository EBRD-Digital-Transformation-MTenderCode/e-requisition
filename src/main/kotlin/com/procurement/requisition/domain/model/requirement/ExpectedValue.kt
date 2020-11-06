package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.isDataTypeMatched

data class ExpectedValue(val value: DynamicValue)

val ExpectedValue?.isPresent
    get() = this != null

val ExpectedValue?.isNotPresent
    get() = !isPresent

fun ExpectedValue.isDataTypeMatched(dataType: DynamicValue.DataType) = value.isDataTypeMatched(dataType)
