package com.procurement.requisition.domain.model

import com.procurement.requisition.domain.extension.toMilliseconds
import com.procurement.requisition.lib.enumerator.EnumElementProvider.Companion.keysAsStrings
import java.time.LocalDateTime

class Ocid private constructor(val underlying: String, val stage: Stage) {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Ocid
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying

    companion object {
        private const val STAGE_POSITION = 4
        private val STAGES: String
            get() = Stage.allowedElements.keysAsStrings()
                .joinToString(separator = "|", prefix = "(", postfix = ")") { it.toUpperCase() }

        val pattern: String
            get() = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-$STAGES-[0-9]{13}\$"

        private val regex = pattern.toRegex()

        fun tryCreateOrNull(value: String): Ocid? =
            if (value.matches(regex)) {
                val stage = Stage.orNull(value.split("-")[STAGE_POSITION])!!
                Ocid(underlying = value, stage = stage)
            } else
                null

        fun generate(cpid: Cpid, stage: Stage, timestamp: LocalDateTime): Ocid =
            Ocid("$cpid-$stage-${timestamp.toMilliseconds()}", stage)
    }
}
