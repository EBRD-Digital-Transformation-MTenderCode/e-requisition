package com.procurement.requisition.domain.model.tender.conversion

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class ConversionRelatesTo(override val key: String) : EnumElementProvider.Element {
    REQUIREMENT("requirement"),
    OBSERVATION("observation"),
    OPTION("option");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConversionRelatesTo>(info = info())
}
