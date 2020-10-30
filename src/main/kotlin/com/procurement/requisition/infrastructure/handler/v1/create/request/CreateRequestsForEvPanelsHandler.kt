package com.procurement.requisition.infrastructure.handler.v1.create.request

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.create.request.CreateRequestsForEvPanelsService
import com.procurement.requisition.application.service.create.request.model.CreateRequestsForEvPanelsCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.create.request.model.convert
import com.procurement.requisition.infrastructure.web.v1.CommandsV1
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class CreateRequestsForEvPanelsHandler(
    override val logger: Logger,
    override val transform: Transform,
    val createRequestsForEvPanelsService: CreateRequestsForEvPanelsService
) : AbstractHandler() {

    override val version: ApiVersion = CommandsV1.apiVersion
    override val action: Action = CommandsV1.CommandType.CREATE_REQUESTS_FOR_EV_PANELS

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = CommandsV1.getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val command = CreateRequestsForEvPanelsCommand(
            cpid = context.cpid.onFailure { return it },
            ocid = context.ocid.onFailure { return it },
            owner = context.owner.onFailure { return it }
        )

        return createRequestsForEvPanelsService.create(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
