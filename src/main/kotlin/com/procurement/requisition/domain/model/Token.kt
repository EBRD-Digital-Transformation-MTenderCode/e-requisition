package com.procurement.requisition.domain.model

import java.util.*

class Token private constructor(val underlying: String) {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): Token? = if (validate(text)) Token(text) else null
        fun generate() = Token(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Token
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
