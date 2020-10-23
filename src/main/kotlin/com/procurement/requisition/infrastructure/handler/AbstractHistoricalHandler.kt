package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.domain.extension.nowDefaultUTC
import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiResponse
import com.procurement.requisition.infrastructure.handler.model.errorResponse
import com.procurement.requisition.infrastructure.service.HistoryEntity
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHistoricalHandler : AbstractHandler() {

    abstract val historyRepository: HistoryRepository

    override fun handle(request: ApiRequest): String {

        val history = historyRepository.getHistory(request.id)
            .onFailure { failure ->
                return errorResponse(
                    logger = logger,
                    failure = failure.reason,
                    version = request.version,
                    id = request.id
                ).serialization(errorMessage = "Error of serialization of failure of save history.")
            }
        if (history != null) return history.data

        val result = when (val result = execute(request)) {
            is Result.Success<*> -> result.value
            is Result.Failure -> errorResponse(
                logger = logger,
                failure = result.reason,
                version = request.version,
                id = request.id
            )
        }.serialization(errorMessage = "Error of serialization ApiResponse.")

        val newHistory = HistoryEntity(
            commandId = request.id,
            action = request.action,
            date = nowDefaultUTC(),
            data = result
        )
        historyRepository.saveHistory(newHistory)
            .onFailure { failure ->
                return errorResponse(
                    logger = logger,
                    failure = failure.reason,
                    version = request.version,
                    id = request.id
                ).serialization(errorMessage = "Error of serialization of failure of save history.")
            }

        return result
    }

    abstract fun execute(request: ApiRequest): Result<ApiResponse, Failure>
}
