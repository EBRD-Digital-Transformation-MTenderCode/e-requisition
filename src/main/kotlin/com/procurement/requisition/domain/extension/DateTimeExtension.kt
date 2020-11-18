package com.procurement.requisition.domain.extension

import com.procurement.requisition.domain.failure.error.DataTimeError
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

private const val PATTERN_OF_FORMATTER = "uuuu-MM-dd'T'HH:mm:ss'Z'"

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_OF_FORMATTER)
    .withResolverStyle(ResolverStyle.STRICT)

fun nowDefaultUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun LocalDateTime.asString(): String = format(formatter)

fun String.toLocalDateTime(): Result<LocalDateTime, DataTimeError> = try {
    LocalDateTime.parse(this, formatter).asSuccess()
} catch (expected: Exception) {
    if (expected.cause == null)
        DataTimeError.InvalidFormat(value = this, pattern = PATTERN_OF_FORMATTER, reason = expected).asFailure()
    else
        DataTimeError.InvalidDateTime(value = this, reason = expected).asFailure()
}

fun LocalDateTime.toMilliseconds(): Long = this.toInstant(ZoneOffset.UTC).toEpochMilli()
