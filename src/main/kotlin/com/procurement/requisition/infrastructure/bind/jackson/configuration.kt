package com.procurement.requisition.infrastructure.bind.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.requisition.domain.model.requirement.RequirementRsValue
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.infrastructure.bind.coefficient.value.CoefficientValueDeserializer
import com.procurement.requisition.infrastructure.bind.coefficient.value.CoefficientValueSerializer
import com.procurement.requisition.infrastructure.bind.observation.measure.ObservationMeasureDeserializer
import com.procurement.requisition.infrastructure.bind.observation.measure.ObservationMeasureSerializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementValueDeserializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementValueSerializer

fun ObjectMapper.configuration() {
    val module = SimpleModule().apply {
        addSerializer(RequirementRsValue::class.java, RequirementValueSerializer())
        addDeserializer(RequirementRsValue::class.java, RequirementValueDeserializer())

        addSerializer(CoefficientValue::class.java, CoefficientValueSerializer())
        addDeserializer(CoefficientValue::class.java, CoefficientValueDeserializer())

        addSerializer(ObservationMeasure::class.java, ObservationMeasureSerializer())
        addDeserializer(ObservationMeasure::class.java, ObservationMeasureDeserializer())
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
