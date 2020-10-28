package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.state

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.get.tender.state.GetTenderStateService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.state.model.GetTenderStateRequest
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.state.model.convert
import com.procurement.requisition.infrastructure.web.v2.CommandsV2
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class GetTenderStateHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getTenderStateService: GetTenderStateService
) : AbstractHandler() {

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = CommandsV2.getParams(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }
            .tryMapping<GetTenderStateRequest>(transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = "RQ-1",
                    version = descriptor.version,
                    id = descriptor.id,
                    body = descriptor.body.asString,
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
                    version = descriptor.version,
                    id = descriptor.id,
                    body = descriptor.body.asString,
                    underlying = failure.description,
                    path = failure.path,
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }

        return getTenderStateService.get(command)
            .flatMap { result ->
                ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
