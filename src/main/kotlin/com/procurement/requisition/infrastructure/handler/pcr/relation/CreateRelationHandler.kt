package com.procurement.requisition.infrastructure.handler.pcr.relation

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.relation.CreateRelationService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.AbstractHistoricalHandler
import com.procurement.requisition.infrastructure.handler.getParams
import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiResponse
import com.procurement.requisition.infrastructure.handler.pcr.relation.model.CreateRelationRequest
import com.procurement.requisition.infrastructure.handler.pcr.relation.model.convert
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class CreateRelationHandler(
    override val logger: Logger,
    override val transform: Transform,
    override val historyRepository: HistoryRepository,
    private val createRelationService: CreateRelationService
) : AbstractHistoricalHandler() {

    override fun execute(request: ApiRequest): Result<ApiResponse, Failure> {

        val params = request.body.asJsonNode.getParams()
            .onFailure { failure -> return failure }
            .tryMapping<CreateRelationRequest>(transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = "RQ-1",
                    version = request.version,
                    id = request.id,
                    body = request.body.asString,
                    underlying = failure.description,
                    path = "params",
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }
            .convert()
            .mapFailure { failure ->
                RequestErrors(
                    code = failure.code,
                    version = request.version,
                    id = request.id,
                    body = request.body.asString,
                    underlying = failure.description,
                    path = failure.path,
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }

        return createRelationService.create(params)
            .map { result ->
                ApiResponse.Success(version = request.version, id = request.id, result = result.convert())
            }
    }
}
