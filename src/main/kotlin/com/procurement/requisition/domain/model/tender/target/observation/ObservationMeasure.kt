package com.procurement.requisition.domain.model.tender.target.observation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.lib.EnumElementProvider


enum class ObservationMeasure(@JsonValue override val key: String) : EnumElementProvider.Key {

    BOOLEAN("boolean"),
    INTEGER("integer"),
    NUMBER("number"),
    STRING("string");

    override fun toString(): String = key

    companion object : EnumElementProvider<ObservationMeasure>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CriterionRelatesTo.orThrow(name)
    }
}