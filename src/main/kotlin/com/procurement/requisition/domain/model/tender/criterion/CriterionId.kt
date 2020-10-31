package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.domain.model.UUID_PATTERN
import com.procurement.requisition.domain.model.isUUID
import java.util.*

class CriterionId private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String) = text.isUUID
        fun orNull(text: String) = if (validate(text)) CriterionId(text) else null
        fun generate() = CriterionId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is CriterionId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
