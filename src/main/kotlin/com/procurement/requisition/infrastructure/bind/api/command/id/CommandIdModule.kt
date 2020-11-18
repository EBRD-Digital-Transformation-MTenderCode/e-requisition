package com.procurement.requisition.infrastructure.bind.api.command.id

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.requisition.infrastructure.api.command.id.CommandId

class CommandIdModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(CommandId::class.java, CommandIdSerializer())
        addDeserializer(CommandId::class.java, CommandIdDeserializer())
    }
}
