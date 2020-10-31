package com.procurement.requisition.infrastructure.bind.coefficient.value

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import java.io.IOException

class CoefficientValueSerializer : JsonSerializer<CoefficientValue>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
        coefficientValue: CoefficientValue,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) = when (coefficientValue) {
        is CoefficientValue.AsString -> jsonGenerator.writeString(coefficientValue.value)
        is CoefficientValue.AsNumber -> jsonGenerator.writeNumber(coefficientValue.toString())
        is CoefficientValue.AsBoolean -> jsonGenerator.writeBoolean(coefficientValue.value)
        is CoefficientValue.AsInteger -> jsonGenerator.writeNumber(coefficientValue.value)
    }
}
