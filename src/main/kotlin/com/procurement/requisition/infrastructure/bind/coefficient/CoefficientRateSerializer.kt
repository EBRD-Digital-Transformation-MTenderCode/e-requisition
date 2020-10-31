package com.procurement.requisition.infrastructure.bind.coefficient

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate
import java.io.IOException
import java.math.BigDecimal

class CoefficientRateSerializer : JsonSerializer<CoefficientRate>() {
    companion object {
        fun serialize(coefficientRate: CoefficientRate): BigDecimal = coefficientRate.rate
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
        CoefficientRate: CoefficientRate,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) {
        val coefficient = serialize(CoefficientRate)
        if (coefficient.stripTrailingZeros().scale() == 0) {
            jsonGenerator.writeNumber(coefficient.longValueExact())
        } else {
            jsonGenerator.writeNumber(coefficient)
        }
    }
}
