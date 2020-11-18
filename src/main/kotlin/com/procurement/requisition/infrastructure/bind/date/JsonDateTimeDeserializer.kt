package com.procurement.requisition.infrastructure.bind.date

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.requisition.domain.extension.toLocalDateTime

import java.time.LocalDateTime

class JsonDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LocalDateTime =
        jsonParser.text
            .toLocalDateTime()
            .orThrow { it.reason }
}
