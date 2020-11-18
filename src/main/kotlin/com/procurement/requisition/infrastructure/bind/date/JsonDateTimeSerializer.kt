package com.procurement.requisition.infrastructure.bind.date

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.requisition.infrastructure.handler.converter.asString
import java.time.LocalDateTime

class JsonDateTimeSerializer : JsonSerializer<LocalDateTime>() {

    override fun serialize(date: LocalDateTime, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        jsonGenerator.writeString(date.asString())
}
