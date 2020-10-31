package com.procurement.requisition.domain.model

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.domain.extension.toMilliseconds
import com.procurement.requisition.lib.enumerator.EnumElementProvider.Companion.keysAsStrings
import java.io.Serializable
import java.time.LocalDateTime

sealed class Ocid(val underlying: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Ocid
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    @JsonValue
    override fun toString(): String = underlying

    class MultiStage private constructor(underlying: String) : Ocid(underlying = underlying) {

        companion object {
            private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}\$".toRegex()

            val pattern: String
                get() = regex.pattern

            fun tryCreateOrNull(value: String): Ocid? =
                if (value.matches(regex)) MultiStage(underlying = value) else null

            fun generate(cpid: Cpid): Ocid = MultiStage(cpid.toString())
        }
    }

    class SingleStage private constructor(underlying: String, val stage: Stage) : Ocid(underlying = underlying) {

        companion object {
            private const val STAGE_POSITION = 4
            private val STAGES: String
                get() = Stage.allowedElements.keysAsStrings()
                    .joinToString(separator = "|", prefix = "(", postfix = ")") { it.toUpperCase() }

            private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-$STAGES-[0-9]{13}\$".toRegex()

            val pattern: String
                get() = regex.pattern

            fun tryCreateOrNull(value: String): Ocid? =
                if (value.matches(regex)) {
                    val stage = Stage.orNull(value.split("-")[STAGE_POSITION])!!
                    SingleStage(underlying = value, stage = stage)
                } else
                    null

            fun generate(cpid: Cpid, stage: Stage, timestamp: LocalDateTime): Ocid =
                SingleStage("$cpid-$stage-${timestamp.toMilliseconds()}", stage)
        }
    }
}
