package com.procurement.requisition.infrastructure.bind.dynamic

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.requisition.domain.model.DynamicValue

class DynamicValueModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(DynamicValue::class.java, DynamicValueSerializer())
        addDeserializer(DynamicValue::class.java, DynamicValueDeserializer())
    }
}
