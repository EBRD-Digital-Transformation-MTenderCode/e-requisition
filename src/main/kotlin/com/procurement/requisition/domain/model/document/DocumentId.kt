package com.procurement.requisition.domain.model.document

import com.procurement.requisition.domain.model.UUID_PATTERN
import com.procurement.requisition.domain.model.isUUID
import java.io.Serializable

class DocumentId private constructor(val underlying: String) : Serializable {

    companion object {
        const val pattern = UUID_PATTERN
        fun validate(text: String): Boolean = true //TODO
        fun orNull(text: String): DocumentId? = if (validate(text)) DocumentId(text) else null
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is DocumentId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
