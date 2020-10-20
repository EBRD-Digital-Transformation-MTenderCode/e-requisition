package com.procurement.requisition.infrastructure.bind.classification

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class ClassificationScheme(@JsonValue override val key: String) : EnumElementProvider.Key {
    CPV("CPV"),
    CPVS("CPVS"),
    GSIN("GSIN"),
    UNSPSC("UNSPSC"),
    CPC("CPC"),
    OKDP("OKDP"),
    OKPD("OKPD");

    override fun toString(): String = key

    companion object : EnumElementProvider<ClassificationScheme>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ClassificationScheme.orThrow(name)
    }
}
