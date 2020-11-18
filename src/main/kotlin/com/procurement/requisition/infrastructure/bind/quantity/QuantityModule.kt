package com.procurement.requisition.infrastructure.bind.quantity

import com.fasterxml.jackson.databind.module.SimpleModule
import java.math.BigDecimal

class QuantityModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(BigDecimal::class.java, QuantitySerializer())
        addDeserializer(BigDecimal::class.java, QuantityDeserializer())
    }
}
