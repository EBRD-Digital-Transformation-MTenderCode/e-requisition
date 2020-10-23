package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiResponse
import com.procurement.requisition.infrastructure.handler.model.errorResponse
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractQueryHandler : AbstractHandler() {

    override fun handle(request: ApiRequest): String = when (val result = execute(request)) {
        is Result.Success<*> -> result.value
        is Result.Failure ->
            errorResponse(logger = logger, failure = result.reason, version = request.version, id = request.id)
    }
        .serialization(errorMessage = "Error of serialization ApiResponse.")

    abstract fun execute(request: ApiRequest): Result<ApiResponse, Failure>
}
