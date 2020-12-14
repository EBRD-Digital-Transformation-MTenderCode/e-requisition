package com.procurement.requisition.infrastructure.handler.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.SetTenderUnsuspendedService
import com.procurement.requisition.application.service.model.command.SetTenderUnsuspendedCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.base.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.converter.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap

@CommandHandler
class SetTenderUnsuspendedHandler(
    override val logger: Logger,
    override val transform: Transform,
    val setTenderUnsuspendService: SetTenderUnsuspendedService
) : AbstractHandlerV1() {

    override val action: Action = Actions.SET_TENDER_UNSUSPENDED

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val command = SetTenderUnsuspendedCommand(
            cpid = context.cpid.onFailure { return it },
            ocid = context.ocid.onFailure { return it },
            phase = context.phase.onFailure { return it }
        )

        return setTenderUnsuspendService.set(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
