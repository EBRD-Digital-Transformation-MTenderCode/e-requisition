package com.procurement.requisition.domain.model.tender.target.observation

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class ObservationMeasure(override val key: String) : EnumElementProvider.Element {

    BOOLEAN("boolean"),
    INTEGER("integer"),
    NUMBER("number"),
    STRING("string");

    override fun toString(): String = key

    companion object : EnumElementProvider<ObservationMeasure>(info = info())
}
