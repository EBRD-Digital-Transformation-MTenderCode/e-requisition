package com.procurement.requisition.infrastructure.bind.observation.measure

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure

class ObservationMeasureSerializer : JsonSerializer<ObservationMeasure>() {

    override fun serialize(
        coefficientValue: ObservationMeasure,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) = when (coefficientValue) {
        is ObservationMeasure.AsString -> jsonGenerator.writeString(coefficientValue.value)
        is ObservationMeasure.AsNumber -> jsonGenerator.writeNumber(coefficientValue.toString())
        is ObservationMeasure.AsBoolean -> jsonGenerator.writeBoolean(coefficientValue.value)
        is ObservationMeasure.AsInteger -> jsonGenerator.writeNumber(coefficientValue.value)
    }
}
