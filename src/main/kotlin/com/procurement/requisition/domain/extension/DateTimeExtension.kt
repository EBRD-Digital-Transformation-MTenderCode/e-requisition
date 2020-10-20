package com.procurement.requisition.domain.extension

import com.procurement.requisition.domain.failure.error.DataTimeParseError
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.*

private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
    .withResolverStyle(ResolverStyle.STRICT)

fun LocalDateTime.format(): String = this.format(formatter)

fun LocalDateTime.toDate(): Date {
    return Date.from(this.toInstant(ZoneOffset.UTC))
}

fun Date.toLocal(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneOffset.UTC)
}

fun String.parseLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, formatter)

fun nowDefaultUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun String.tryParseLocalDateTime(): Result<LocalDateTime, DataTimeParseError> = try {
    Result.success(parseLocalDateTime())
} catch (expected: Exception) {
    if (expected.cause == null)
        failure(DataTimeParseError.InvalidFormat(value = this, pattern = FORMAT_PATTERN, reason = expected))
    else
        failure(DataTimeParseError.InvalidDateTime(value = this, reason = expected))
}

fun LocalDateTime.toMilliseconds(): Long = this.toInstant(ZoneOffset.UTC).toEpochMilli()
