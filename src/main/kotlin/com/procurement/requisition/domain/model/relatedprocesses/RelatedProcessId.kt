package com.procurement.requisition.domain.model.relatedprocesses

import com.procurement.requisition.domain.model.UUID_PATTERN
import com.procurement.requisition.domain.model.isUUID
import java.io.Serializable
import java.util.*

class RelatedProcessId private constructor(val underlying: String) : Serializable {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = text.isUUID
        fun orNull(text: String): RelatedProcessId? = if (validate(text)) RelatedProcessId(text) else null
        fun generate() = RelatedProcessId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is RelatedProcessId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
