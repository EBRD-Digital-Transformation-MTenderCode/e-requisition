package com.procurement.requisition.domain.extension

import com.procurement.requisition.domain.failure.error.DataTimeError
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

private const val PATTERN_OF_FORMATTER = "uuuu-MM-dd'T'HH:mm:ss'Z'"

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_OF_FORMATTER)
    .withResolverStyle(ResolverStyle.STRICT)

fun LocalDateTime.format(): String = format(formatter)

fun nowDefaultUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun String.parseLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, formatter)

fun String.tryParseLocalDateTime(): Result<LocalDateTime, DataTimeError> = try {
    Result.success(parseLocalDateTime())
} catch (expected: Exception) {
    if (expected.cause == null)
        failure(DataTimeError.InvalidFormat(value = this, pattern = PATTERN_OF_FORMATTER, reason = expected))
    else
        failure(DataTimeError.InvalidDateTime(value = this, reason = expected))
}

fun LocalDateTime.toMilliseconds(): Long = this.toInstant(ZoneOffset.UTC).toEpochMilli()
