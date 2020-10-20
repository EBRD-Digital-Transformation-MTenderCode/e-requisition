package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.lib.fail.Failure

sealed class DataTimeParseError(number: String, reason: Exception) :
    Failure.Error(reason) {

    override val code: String = "PE-DT-$number"

    companion object {
        private const val DESCRIPTION_PREFIX = "Date-Time parsing error."
    }

    class InvalidFormat(val value: String, val pattern: String, reason: Exception) :
        DataTimeParseError(number = "01", reason = reason) {

        override val description: String
            get() = "$DESCRIPTION_PREFIX Invalid actual format of value '$value', expected pattern of a format '$pattern'."
    }

    class InvalidDateTime(val value: String, reason: Exception) :
        DataTimeParseError(number = "02", reason = reason) {

        override val description: String
            get() = "$DESCRIPTION_PREFIX Invalid actual value '$value'."
    }
}
