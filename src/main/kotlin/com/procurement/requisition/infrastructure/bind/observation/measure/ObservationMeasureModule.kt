package com.procurement.requisition.infrastructure.bind.observation.measure

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure

class ObservationMeasureModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ObservationMeasure::class.java, ObservationMeasureSerializer())
        addDeserializer(ObservationMeasure::class.java, ObservationMeasureDeserializer())
    }
}
