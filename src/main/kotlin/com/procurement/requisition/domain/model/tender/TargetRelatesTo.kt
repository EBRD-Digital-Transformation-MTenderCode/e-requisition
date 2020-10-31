package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class TargetRelatesTo(override val key: String) : EnumElementProvider.Element {

    ITEM("item"),
    LOT("lot");

    override fun toString(): String = key

    companion object : EnumElementProvider<TargetRelatesTo>(info = info())
}
