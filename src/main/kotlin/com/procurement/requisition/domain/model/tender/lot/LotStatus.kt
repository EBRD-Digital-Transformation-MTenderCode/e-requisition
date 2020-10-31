package com.procurement.requisition.domain.model.tender.lot

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class LotStatus(override val key: String) : EnumElementProvider.Element {
    PLANNING("planning"),
    PLANNED("planned"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    UNSUCCESSFUL("unsuccessful"),
    COMPLETE("complete");

    override fun toString(): String = key

    companion object : EnumElementProvider<LotStatus>(info = info())
}
