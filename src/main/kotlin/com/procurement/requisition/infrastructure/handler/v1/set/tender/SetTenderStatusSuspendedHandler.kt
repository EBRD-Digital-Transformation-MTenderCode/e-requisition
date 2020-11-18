package com.procurement.requisition.infrastructure.handler.v1.set.tender

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.set.tender.SetTenderStatusSuspendedService
import com.procurement.requisition.application.service.set.tender.model.SetTenderStatusSuspendedCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.set.tender.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class SetTenderStatusSuspendedHandler(
    override val logger: Logger,
    override val transform: Transform,
    val setTenderSuspendedService: SetTenderStatusSuspendedService
) : AbstractHandlerV1() {

    override val action: Action = Actions.SET_TENDER_SUSPENDED

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val command = SetTenderStatusSuspendedCommand(
            cpid = context.cpid.onFailure { return it },
            ocid = context.ocid.onFailure { return it }
        )

        return setTenderSuspendedService.set(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
