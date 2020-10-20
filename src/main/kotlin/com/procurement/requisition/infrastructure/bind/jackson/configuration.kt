package com.procurement.requisition.infrastructure.bind.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientValue
import com.procurement.requisition.domain.model.requirement.RequirementRsValue
import com.procurement.requisition.infrastructure.bind.coefficient.value.CoefficientValueDeserializer
import com.procurement.requisition.infrastructure.bind.coefficient.value.CoefficientValueSerializer
import com.procurement.requisition.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.requisition.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementValueDeserializer
import com.procurement.requisition.infrastructure.bind.requirement.RequirementValueSerializer
import java.time.LocalDateTime

fun ObjectMapper.configuration() {
    val module = SimpleModule().apply {
        /**
         * Serializer/Deserializer for LocalDateTime type
         */
        addSerializer(LocalDateTime::class.java, JsonDateTimeSerializer())
        addDeserializer(LocalDateTime::class.java, JsonDateTimeDeserializer())

        addSerializer(RequirementRsValue::class.java, RequirementValueSerializer())
        addDeserializer(RequirementRsValue::class.java, RequirementValueDeserializer())

        addSerializer(CoefficientValue::class.java, CoefficientValueSerializer())
        addDeserializer(CoefficientValue::class.java, CoefficientValueDeserializer())
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
