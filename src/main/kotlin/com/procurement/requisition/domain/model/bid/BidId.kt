package com.procurement.requisition.domain.model.bid

import com.procurement.requisition.domain.model.UUID_PATTERN
import com.procurement.requisition.domain.model.isUUID

class BidId private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String) = text.isUUID
        fun orNull(text: String) = if (validate(text)) BidId(text) else null
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is BidId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
