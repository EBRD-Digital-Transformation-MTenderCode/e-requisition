package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

fun <T, E : JsonErrors> Result<T, E>.repath(path: String): Result<T, E> = when (this) {
    is Result.Success -> this
    is Result.Failure -> {
        (this.reason as JsonErrors).repath(path)
        this
    }
}

fun <E : JsonErrors> E.repath(path: String): E {
    this.path.repath(path)
    return this
}

sealed class JsonErrors(override val code: String, val path: Path = Path()) : Failure.Error() {

    class Path(path: String? = null) {
        private val elements = mutableListOf<String>()
            .apply {
                if (path != null) this.add(path)
            }

        fun repath(path: String) {
            elements.add(path)
        }

        fun asString(): String = buildString {
            this.append("#")
            for (i in elements.size - 1..0) {
                this.append(elements[i])
            }
        }
    }

    class Parsing(override val reason: Exception?) : JsonErrors(code = "DR-1") {

        override val description: String
            get() = "Error of parsing."
    }

    class MissingRequiredAttribute(override val reason: Exception? = null) :
        JsonErrors(code = "DR-1") {

        override val description: String
            get() = "Missing required attribute '${path.asString()}'."
    }

    class DataTypeMismatch(val expected: String, val actual: String, override val reason: Exception? = null) :
        JsonErrors(code = "DR-2") {

        override val description: String
            get() = "Data type mismatch of '${path.asString()}' attribute. Expected data type: '$expected', actual data type: '$actual'."
    }

    class UnknownValue(
        val expectedValues: Collection<String>,
        val actualValue: String,
        override val reason: Exception? = null
    ) : JsonErrors(code = "DR-3") {

        override val description: String
            get() = "Attribute value mismatch with one of enum expected values. Expected values: '${expectedValues.joinToString()}', actual value: '$actualValue'."
    }

    class DataFormatMismatch(
        val expectedFormat: String,
        val actualValue: String,
        override val reason: Exception? = null
    ) : JsonErrors(code = "DR-4") {

        override val description: String
            get() = "Data format mismatch. Expected data format: '$expectedFormat', actual value: '$actualValue'."
    }

    class EmptyArray(override val reason: Exception? = null) :
        JsonErrors(code = "DR-10") {

        override val description: String
            get() = "Array by path '${path.asString()}' is empty."
    }

    class DateTimeInvalid(val value: String, override val reason: Exception? = null) : JsonErrors(code = "DR-13") {

        override val description: String
            get() = "Invalid date-time '$value' by path '${path.asString()}'."
    }
}
