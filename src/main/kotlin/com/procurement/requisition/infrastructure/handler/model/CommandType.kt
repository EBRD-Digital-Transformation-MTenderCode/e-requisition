package com.procurement.requisition.infrastructure.handler.model

import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class CommandType(override val key: String) : EnumElementProvider.Element, Action {

    CREATE_PCR("createPcr"),
    VALIDATE_PCR_DATA("validatePcrData");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandType>(info = info())
}
