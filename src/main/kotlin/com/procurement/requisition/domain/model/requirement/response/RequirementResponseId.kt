package com.procurement.requisition.domain.model.requirement.response

class RequirementResponseId private constructor(val underlying: String) {

    companion object {
        const val pattern = ".*"
        private val regex = pattern.toRegex()

        fun validate(text: String) = text.matches(regex)
        fun orNull(text: String) = if (validate(text)) RequirementResponseId(text) else null
    }

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is RequirementResponseId
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying
}
