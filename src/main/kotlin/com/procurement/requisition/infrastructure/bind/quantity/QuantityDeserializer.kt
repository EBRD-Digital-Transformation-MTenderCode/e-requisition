package com.procurement.requisition.infrastructure.bind.quantity

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.requisition.infrastructure.exception.QuantityValueException
import java.io.IOException
import java.math.BigDecimal

class QuantityDeserializer : JsonDeserializer<BigDecimal>() {
    companion object {
        private val MAX_ALLOWED_SCALE = 3
        fun deserialize(text: String): BigDecimal = try {
            val scale = BigDecimal(text).scale()
            if (scale > MAX_ALLOWED_SCALE)
                throw IllegalArgumentException("Attribute 'quantity' is an invalid scale '$scale', the maximum scale: '$MAX_ALLOWED_SCALE'.")

            BigDecimal(text).setScale(MAX_ALLOWED_SCALE)
        } catch (exception: Exception) {
            throw QuantityValueException(quantity = text, description = exception.message ?: "")
        }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): BigDecimal {
        if (jsonParser.currentToken != JsonToken.VALUE_NUMBER_FLOAT && jsonParser.currentToken != JsonToken.VALUE_NUMBER_INT) {
            throw QuantityValueException(
                quantity = "\"${jsonParser.text}\"",
                description = "The value must be a real number."
            )
        }
        return deserialize(jsonParser.text)
    }
}
