package com.procurement.requisition.infrastructure.bind.observation.measure

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import java.math.BigDecimal

class ObservationMeasureDeserializer : JsonDeserializer<ObservationMeasure>() {

    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): ObservationMeasure = when (jsonParser.currentToken) {
        JsonToken.VALUE_STRING -> ObservationMeasure.of(jsonParser.text)
        JsonToken.VALUE_FALSE -> ObservationMeasure.of(false)
        JsonToken.VALUE_TRUE -> ObservationMeasure.of(true)
        JsonToken.VALUE_NUMBER_INT -> ObservationMeasure.of(jsonParser.longValue)
        JsonToken.VALUE_NUMBER_FLOAT -> ObservationMeasure.of(BigDecimal(jsonParser.text))
        else -> throw ObservationMeasureException(coefficientValue = jsonParser.text, description = "Incorrect type")
    }
}
