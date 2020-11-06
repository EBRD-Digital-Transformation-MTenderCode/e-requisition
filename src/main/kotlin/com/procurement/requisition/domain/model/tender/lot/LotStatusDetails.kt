package com.procurement.requisition.domain.model.tender.lot

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class LotStatusDetails(override val key: String) : EnumElementProvider.Element {

    UNSUCCESSFUL("unsuccessful"),
    AWARDED("awarded"),
    CANCELLED("cancelled"),
    NONE("") {
        override val isNeutralElement: Boolean = true
    };

    override fun toString(): String = key

    companion object : EnumElementProvider<LotStatusDetails>(info = info())
}
