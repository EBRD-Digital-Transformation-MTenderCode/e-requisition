package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform

abstract class AbstractHandler : Handler {

    abstract val transform: Transform
    abstract val logger: Logger

    protected fun <T> T.serialization(errorMessage: String) = transform.trySerialization(this)
        .doOnError { failure -> logger.error(message = errorMessage, exception = failure.reason) }
        .getOrElse("Internal Server Error.")
}
