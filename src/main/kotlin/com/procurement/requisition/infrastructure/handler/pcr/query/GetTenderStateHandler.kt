package com.procurement.requisition.infrastructure.handler.pcr.query

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.get.tender.state.GetTenderStateService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.AbstractQueryHandler
import com.procurement.requisition.infrastructure.handler.getParams
import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiResponse
import com.procurement.requisition.infrastructure.handler.pcr.query.model.GetTenderStateRequest
import com.procurement.requisition.infrastructure.handler.pcr.query.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class GetTenderStateHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getTenderStateService: GetTenderStateService
) : AbstractQueryHandler() {

    override fun execute(request: ApiRequest): Result<ApiResponse, Failure> {

        val command = request.node.getParams()
            .onFailure { failure -> return failure }
            .tryMapping<GetTenderStateRequest>(transform)
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

        return getTenderStateService.get(command)
            .map { result ->
                ApiResponse.Success(version = request.version, id = request.id, result = result.convert())
            }
    }
}
