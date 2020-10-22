package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class TenderStatus(override val key: String) : EnumElementProvider.Element {
    ACTIVE("active"),
    CANCELLED("cancelled"),
    COMPLETE("complete"),
    PLANNED("planned"),
    PLANNING("planning"),
    UNSUCCESSFUL("unsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<TenderStatus>(info = info())
}
