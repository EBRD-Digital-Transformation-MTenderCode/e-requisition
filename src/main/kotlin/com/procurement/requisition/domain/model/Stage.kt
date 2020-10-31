package com.procurement.requisition.domain.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class Stage(override val key: String) : EnumElementProvider.Element {

    AC("AC"),
    AP("AP"),
    EI("EI"),
    EV("EV"),
    FE("FE"),
    FS("FS"),
    NP("NP"),
    PC("PC"),
    PN("PN"),
    TP("TP");

    override fun toString(): String = key

    companion object : EnumElementProvider<Stage>(info = info())
}
