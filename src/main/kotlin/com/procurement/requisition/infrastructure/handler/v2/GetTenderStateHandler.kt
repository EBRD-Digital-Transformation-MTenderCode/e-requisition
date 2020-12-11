package com.procurement.requisition.infrastructure.handler.v2

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.GetTenderStateService
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v2.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.requisition.infrastructure.handler.v2.model.request.GetTenderStateRequest
import com.procurement.requisition.infrastructure.handler.v2.converter.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap

@CommandHandler
class GetTenderStateHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getTenderStateService: GetTenderStateService
) : AbstractHandlerV2() {

    override val action: Action = Actions.GET_TENDER_STATE

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = descriptor.getCommand(GetTenderStateRequest::convert)
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
