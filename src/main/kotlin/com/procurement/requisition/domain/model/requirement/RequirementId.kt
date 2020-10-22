package com.procurement.requisition.domain.model.requirement

import java.util.*

typealias RequirementId = String

fun generateRequirementId(): RequirementId = UUID.randomUUID().toString()

/*class RequirementId private constructor(val underlying: String) {

    companion object {
        val pattern = UUID_PATTERN
        fun validate(text: String) = text.isUUID
        fun orNull(text: String) = if (validate(text)) RequirementId(text) else null
        fun generate() = RequirementId(UUID.randomUUID().toString())
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is RequirementId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}*/
