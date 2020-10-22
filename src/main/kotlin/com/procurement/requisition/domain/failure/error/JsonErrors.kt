package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.lib.fail.Failure

sealed class JsonErrors(override val code: String, val path: String) : Failure.Error() {

    class MissingRequiredAttribute(path: String, override val reason: Exception?) :
        JsonErrors(code = "DR-1", path = path) {

        override val description: String
            get() = "Missing required attribute '$path'."
    }

    class DataTypeMismatch(path: String, val expected: String, val actual: String, override val reason: Exception?) :
        JsonErrors(code = "DR-2", path = path) {

        override val description: String
            get() = "Data type mismatch of '$path' attribute. Expected data type: '$expected', actual data type: '$actual'."
    }

    class UnknownValue(
        path: String,
        val expectedValues: Collection<String>,
        val actualValue: String,
        override val reason: Exception? = null
    ) : JsonErrors(code = "DR-3", path = path) {

        override val description: String
            get() = "Attribute value mismatch with one of enum expected values. Expected values: '${expectedValues.joinToString()}', actual value: '$actualValue'."
    }

    class DataFormatMismatch(
        path: String,
        val expectedFormat: String,
        val actualValue: String,
        override val reason: Exception? = null
    ) : JsonErrors(code = "DR-4", path = path) {

        override val description: String
            get() = "Data format mismatch. Expected data format: '$expectedFormat', actual value: '$actualValue'."
    }

    class EmptyArray(path: String, override val reason: Exception? = null) :
        JsonErrors(code = "DR-10", path = path) {

        override val description: String
            get() = "Array by path '$path' is empty."
    }

    class DateTimeInvalid(
        val value: String,
        path: String,
        override val reason: Exception? = null
    ) : JsonErrors(code = "DR-13", path = path) {

        override val description: String
            get() = "Invalid date-time '$value' by path '$path'."
    }
}
