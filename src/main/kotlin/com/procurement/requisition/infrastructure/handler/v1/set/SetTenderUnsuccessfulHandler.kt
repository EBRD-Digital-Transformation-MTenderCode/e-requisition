package com.procurement.requisition.infrastructure.handler.v1.set

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.set.SetTenderUnsuccessfulService
import com.procurement.requisition.application.service.set.model.SetTenderUnsuccessfulCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.create.request.model.convert
import com.procurement.requisition.infrastructure.handler.v1.set.model.convert
import com.procurement.requisition.infrastructure.web.v1.CommandsV1
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class SetTenderUnsuccessfulHandler(
    override val logger: Logger,
    override val transform: Transform,
    val setTenderUnsuccessfulService: SetTenderUnsuccessfulService
) : AbstractHandler() {

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = CommandsV1.getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val command = SetTenderUnsuccessfulCommand(
            cpid = context.cpid.onFailure { return it },
            ocid = context.ocid.onFailure { return it },
            startDate = context.startDate.onFailure { return it }
        )

        return setTenderUnsuccessfulService.set(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
