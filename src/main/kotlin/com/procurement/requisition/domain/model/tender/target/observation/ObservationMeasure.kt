package com.procurement.requisition.domain.model.tender.target.observation

import java.math.BigDecimal

sealed class ObservationMeasure {

    companion object {
        fun of(value: Boolean): ObservationMeasure = AsBoolean(value)
        fun of(value: String): ObservationMeasure = AsString(value)
        fun of(value: BigDecimal): ObservationMeasure = AsNumber(value)
        fun of(value: Long): ObservationMeasure = AsInteger(value)
    }

    class AsBoolean(val value: Boolean) : ObservationMeasure() {

        override fun equals(other: Any?): Boolean = if (this === other)
            true
        else
            other is AsBoolean
                && this.value == other.value

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = value.toString()
    }

    class AsString(val value: String) : ObservationMeasure() {

        override fun equals(other: Any?): Boolean = if (this === other)
            true
        else
            other is AsString
                && this.value == other.value

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = value
    }

    class AsNumber private constructor(val value: BigDecimal) : ObservationMeasure() {

        companion object {
            const val AVAILABLE_SCALE = 3
            private const val STRING_FORMAT = "%.${AVAILABLE_SCALE}f"

            operator fun invoke(value: BigDecimal): AsNumber {
                checkScale(value)
                return AsNumber(value)
            }

            private fun checkScale(value: BigDecimal) {
                val scale = value.scale()
                require(scale <= AVAILABLE_SCALE) {
                    "The 'coefficient value' is an invalid scale '$scale', the maximum scale: '${AVAILABLE_SCALE}'."
                }
            }
        }

        override fun equals(other: Any?): Boolean = if (this === other)
            true
        else
            other is AsNumber
                && this.value == other.value

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = STRING_FORMAT.format(value)
    }

    class AsInteger(val value: Long) : ObservationMeasure() {

        override fun equals(other: Any?): Boolean = if (this === other)
            true
        else
            other is AsInteger
                && this.value == other.value

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = value.toString()
    }
}
