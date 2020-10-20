package com.procurement.requisition.infrastructure.handler

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class CommandType(@JsonValue override val key: String) : EnumElementProvider.Key, Action {

    CREATE_PCR("createPcr"),
    VALIDATE_PCR_DATA("validatePcrData");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
