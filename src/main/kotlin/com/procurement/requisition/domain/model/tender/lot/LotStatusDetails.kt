package com.procurement.requisition.domain.model.tender.lot

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class LotStatusDetails(override val key: String) : EnumElementProvider.Element {
    AWARDED("awarded"),
    CANCELLED("cancelled"),
    EMPTY("empty"),
    RECONSIDERATION("reconsideration"),
    UNSUCCESSFUL("unsuccessful"),

    NONE("") {
        override val isNeutralElement: Boolean = true
    };

    override fun toString(): String = key

    companion object : EnumElementProvider<LotStatusDetails>(info = info())
}
