package com.procurement.requisition.domain.model.tender.target.observation

import com.procurement.requisition.domain.model.UUID_PATTERN
import com.procurement.requisition.domain.model.isUUID
import java.util.*

class ObservationId private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String) = text.isUUID
        fun orNull(text: String) = if (validate(text)) ObservationId(text) else null
        fun generate() = ObservationId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ObservationId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
