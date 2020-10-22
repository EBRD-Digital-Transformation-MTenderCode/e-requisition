package com.procurement.requisition.infrastructure.handler.converter

import com.procurement.requisition.domain.extension.format
import com.procurement.requisition.domain.extension.tryParseLocalDateTime
import com.procurement.requisition.domain.failure.error.DataTimeParseError
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.lib.enumerator.EnumElementProvider.Companion.keysAsStrings
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import java.time.LocalDateTime

fun <T> String.asEnum(
    target: EnumElementProvider<T>,
    path: String,
    allowedElements: Set<T> = target.allowedElements
): Result<T, JsonErrors.UnknownValue> where T : Enum<T>,
                                            T : EnumElementProvider.Element = target.orNull(this)
    ?.takeIf { it in allowedElements }
    ?.asSuccess()
    ?: Result.failure(
        JsonErrors.UnknownValue(
            path = path,
            expectedValues = allowedElements.keysAsStrings(),
            actualValue = this,
            reason = null
        )
    )

fun <T> T.asStringOrNull(): String? where T : Enum<T>,
                                          T : EnumElementProvider.Element = takeIf { !it.isNeutralElement }?.key

fun <T> T.asString(): String where T : Enum<T>,
                                   T : EnumElementProvider.Element = key

fun String.asLocalDateTime(path: String): Result<LocalDateTime, JsonErrors> = tryParseLocalDateTime()
    .mapFailure { failure ->
        when (failure) {
            is DataTimeParseError.InvalidFormat -> JsonErrors.DataFormatMismatch(
                path = path,
                actualValue = failure.value,
                expectedFormat = failure.pattern,
                reason = failure.reason
            )

            is DataTimeParseError.InvalidDateTime -> JsonErrors.DateTimeInvalid(
                path = path,
                value = failure.value,
                reason = failure.reason
            )
        }
    }

fun LocalDateTime.asString() = format()
