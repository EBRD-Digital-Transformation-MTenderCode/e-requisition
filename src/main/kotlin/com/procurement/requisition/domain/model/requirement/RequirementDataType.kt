package com.procurement.requisition.domain.model.requirement

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class RequirementDataType(@JsonValue override val key: String) : EnumElementProvider.Element {

    BOOLEAN("boolean"),
    INTEGER("integer"),
    NUMBER("number"),
    STRING("string");

    override fun toString(): String = key

    companion object : EnumElementProvider<RequirementDataType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = RequirementDataType.orThrow(name)
    }
}
