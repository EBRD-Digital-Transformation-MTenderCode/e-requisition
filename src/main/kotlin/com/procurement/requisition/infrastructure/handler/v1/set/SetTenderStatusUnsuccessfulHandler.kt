package com.procurement.requisition.infrastructure.handler.v1.set

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.set.SetTenderStatusUnsuccessfulService
import com.procurement.requisition.application.service.set.model.SetTenderStatusUnsuccessfulCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.set.model.convert
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class SetTenderStatusUnsuccessfulHandler(
    override val logger: Logger,
    override val transform: Transform,
    val setTenderStatusUnsuccessfulService: SetTenderStatusUnsuccessfulService
) : AbstractHandlerV1() {

    override val action: Action = Actions.SET_TENDER_STATUS_UNSUCCESSFUL

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val command = SetTenderStatusUnsuccessfulCommand(
            cpid = context.cpid.onFailure { return it },
            ocid = context.ocid.onFailure { return it },
            startDate = context.startDate.onFailure { return it }
        )

        return setTenderStatusUnsuccessfulService.set(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
