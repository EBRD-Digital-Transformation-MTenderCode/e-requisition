package com.procurement.requisition.domain.model.tender.conversion

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class ConversionRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {
    REQUIREMENT("requirement"),
    OBSERVATION("observation"),
    OPTION("option");

    override fun toString(): String = key

    companion object : EnumElementProvider<ConversionRelatesTo>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ConversionRelatesTo.orThrow(name)
    }
}
