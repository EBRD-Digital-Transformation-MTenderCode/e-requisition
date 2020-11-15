package com.procurement.requisition.domain.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class MainProcurementCategory(override val key: String) : EnumElementProvider.Element {

    GOODS("goods"),
    WORKS("works"),
    SERVICES("services");

    override fun toString(): String = key

    companion object : EnumElementProvider<MainProcurementCategory>(info = info())
}
