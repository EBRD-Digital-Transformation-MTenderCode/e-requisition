package com.procurement.requisition.lib.fail

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.lib.EnumElementProvider

sealed class Failure(val reason: Exception?) {
    abstract val code: String
    abstract val description: String
    abstract val loggingMessage: String

    abstract fun logging(logger: Logger)

    abstract class Error(reason: Exception?) : Failure(reason = reason) {

        override val loggingMessage: String
            get() = "Error Code: '$code', description: '$description'."

        override fun logging(logger: Logger) {
            logger.error(message = loggingMessage, exception = reason)
        }
    }

    abstract class Incident(val level: Level, number: String, reason: Exception?) :
        Failure(reason = reason) {

        override val code: String = "INC-$number"

        override val loggingMessage: String
            get() = "Incident Code: '$code', description: '$description'."

        override fun logging(logger: Logger) {
            when (level) {
                Level.ERROR -> logger.error(message = loggingMessage, exception = reason)
                Level.WARNING -> logger.warn(message = loggingMessage, exception = reason)
                Level.INFO -> logger.info(message = loggingMessage, exception = reason)
            }
        }

        enum class Level(@JsonValue override val key: String) : EnumElementProvider.Key {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}
