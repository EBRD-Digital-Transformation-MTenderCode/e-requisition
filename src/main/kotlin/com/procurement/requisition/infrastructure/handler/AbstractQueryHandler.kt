package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.web.dto.ApiRequest
import com.procurement.requisition.infrastructure.web.dto.ApiResponse
import com.procurement.requisition.infrastructure.web.dto.errorResponse
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractQueryHandler : Handler {
    abstract val logger: Logger
    abstract val transform: Transform

    override fun handle(request: ApiRequest): String = when (val result = execute(request)) {
        is Result.Success<*> ->
            ApiResponse.Success(version = request.version, id = request.id, result = result.value)

        is Result.Failure -> errorResponse(failure = result.reason, version = request.version, id = request.id)
    }
        .trySerialization(transform)
        .doOnError { failure ->
            logger.error(message = "Error of serialization Api-Response", exception = failure.reason)
        }
        .getOrElse("Internal Server Error")

    abstract fun execute(request: ApiRequest): Result<ApiResponse, Failure>
}
