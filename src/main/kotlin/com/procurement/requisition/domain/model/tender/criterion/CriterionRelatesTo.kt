package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class CriterionRelatesTo(override val key: String) : EnumElementProvider.Element {

    ITEM("item"),
    LOT("lot");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriterionRelatesTo>(info = info())
}
