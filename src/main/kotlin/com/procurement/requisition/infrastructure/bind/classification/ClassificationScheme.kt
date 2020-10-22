package com.procurement.requisition.infrastructure.bind.classification

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class ClassificationScheme(override val key: String) : EnumElementProvider.Element {
    CPV("CPV"),
    CPVS("CPVS"),
    GSIN("GSIN"),
    UNSPSC("UNSPSC"),
    CPC("CPC"),
    OKDP("OKDP"),
    OKPD("OKPD");

    override fun toString(): String = key

    companion object : EnumElementProvider<ClassificationScheme>(info = info())
}
