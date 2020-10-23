package com.procurement.requisition.infrastructure.handler.pcr.create

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.create.CreatePCRService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.AbstractHistoricalHandler
import com.procurement.requisition.infrastructure.handler.getParams
import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiResponse
import com.procurement.requisition.infrastructure.handler.pcr.create.model.CreatePCRRequest
import com.procurement.requisition.infrastructure.handler.pcr.create.model.convert
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class CreatePCRHandler(
    override val logger: Logger,
    override val transform: Transform,
    override val historyRepository: HistoryRepository,
    val createPCRService: CreatePCRService
) : AbstractHistoricalHandler() {

    override fun execute(request: ApiRequest): Result<ApiResponse, Failure> {

        val params = request.node.getParams()
            .onFailure { failure -> return failure }
            .tryMapping<CreatePCRRequest>(transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = "RQ-1",
                    version = request.version,
                    id = request.id,
                    body = request.body,
                    underlying = failure.description,
                    path = "params",
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }
            .convert()
            .onFailure { failure -> return failure }

        return createPCRService.create(params)
            .map { result ->
                ApiResponse.Success(version = request.version, id = request.id, result = result.convert())
            }
    }
}
