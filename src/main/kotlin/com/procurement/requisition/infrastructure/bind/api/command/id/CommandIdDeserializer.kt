package com.procurement.requisition.infrastructure.bind.api.command.id

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.requisition.infrastructure.api.command.id.CommandId

class CommandIdDeserializer : JsonDeserializer<CommandId>() {
    companion object {
        fun deserialize(text: String): CommandId = CommandId(text)
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): CommandId =
        deserialize(jsonParser.text)
}
