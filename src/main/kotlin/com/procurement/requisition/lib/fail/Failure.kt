package com.procurement.requisition.lib.fail

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.lib.enumerator.EnumElementProvider

sealed class Failure {
    abstract val code: String
    abstract val description: String
    abstract val reason: Exception?
    abstract val loggingMessage: String

    abstract fun logging(logger: Logger)

    abstract class Error : Failure() {

        override val loggingMessage: String
            get() = "Error Code: '$code', description: '$description'."

        override fun logging(logger: Logger) {
            logger.error(message = loggingMessage, exception = reason)
        }
    }

    abstract class Incident(val level: Level, number: String) : Failure() {

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

        enum class Level(override val key: String) : EnumElementProvider.Element {
            ERROR("error"),
            WARNING("warning"),
            INFO("info");

            companion object : EnumElementProvider<Level>(info = info())
        }
    }
}
