package com.procurement.requisition.infrastructure.bind.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.infrastructure.bind.dynamic.DynamicValueDeserializer
import com.procurement.requisition.infrastructure.bind.dynamic.DynamicValueSerializer
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateDeserializer
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateSerializer
import com.procurement.requisition.infrastructure.bind.command.CommandIdDeserializer
import com.procurement.requisition.infrastructure.bind.command.CommandIdSerializer
import com.procurement.requisition.infrastructure.bind.observation.measure.ObservationMeasureDeserializer
import com.procurement.requisition.infrastructure.bind.observation.measure.ObservationMeasureSerializer
import com.procurement.requisition.infrastructure.handler.model.CommandId

fun ObjectMapper.configuration() {
    val module = SimpleModule().apply {
        addSerializer(CommandId::class.java, CommandIdSerializer())
        addDeserializer(CommandId::class.java, CommandIdDeserializer())

        addSerializer(CoefficientRate::class.java, CoefficientRateSerializer())
        addDeserializer(CoefficientRate::class.java, CoefficientRateDeserializer())

        addSerializer(ObservationMeasure::class.java, ObservationMeasureSerializer())
        addDeserializer(ObservationMeasure::class.java, ObservationMeasureDeserializer())

        addSerializer(DynamicValue::class.java, DynamicValueSerializer())
        addDeserializer(DynamicValue::class.java, DynamicValueDeserializer())
    }

    this.registerModule(module)
    this.registerModule(KotlinModule())
    this.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    this.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
    this.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false)
    this.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}
