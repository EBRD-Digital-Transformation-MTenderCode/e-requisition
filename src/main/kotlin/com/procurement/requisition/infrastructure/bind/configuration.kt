package com.procurement.requisition.infrastructure.bind

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.requisition.infrastructure.bind.api.command.id.CommandIdModule
import com.procurement.requisition.infrastructure.bind.api.version.ApiVersionModule
import com.procurement.requisition.infrastructure.bind.coefficient.CoefficientRateModule
import com.procurement.requisition.infrastructure.bind.date.JsonDateTimeModule
import com.procurement.requisition.infrastructure.bind.dynamic.DynamicValueModule
import com.procurement.requisition.infrastructure.bind.observation.measure.ObservationMeasureModule
import com.procurement.requisition.infrastructure.bind.quantity.QuantityModule

fun ObjectMapper.configuration() {
    registerModule(ApiVersionModule())
    registerModule(CommandIdModule())
    registerModule(JsonDateTimeModule())
    registerModule(CoefficientRateModule())
    registerModule(ObservationMeasureModule())
    registerModule(DynamicValueModule())
    registerModule(QuantityModule())
    registerModule(KotlinModule())

    configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false)
    configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false)
    nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}
