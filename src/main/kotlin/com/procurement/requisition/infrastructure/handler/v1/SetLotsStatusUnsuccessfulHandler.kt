package com.procurement.requisition.infrastructure.handler.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.set.SetLotsStatusUnsuccessfulService
import com.procurement.requisition.application.service.set.model.SetLotsStatusUnsuccessfulCommand
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.base.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.converter.convert
import com.procurement.requisition.infrastructure.handler.v1.model.request.SetLotsStatusUnsuccessfulRequest
import com.procurement.requisition.infrastructure.handler.v1.model.request.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap

@CommandHandler
class SetLotsStatusUnsuccessfulHandler(
    override val logger: Logger,
    override val transform: Transform,
    val setLotsStatusUnsuccessfulService: SetLotsStatusUnsuccessfulService
) : AbstractHandlerV1() {

    override val action: Action = Actions.SET_LOTS_STATUS_UNSUCCESSFUL

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val data = getData<SetLotsStatusUnsuccessfulRequest>(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid.onFailure { return it }
        val ocid = context.ocid.onFailure { return it }
        val startDate = context.startDate.onFailure { return it }
        val lots = data.lots
            .mapIndexed { idx, lot ->
                lot.convert().onFailure { return it.repath(path = "/data/unsuccessfulLots[$idx]") }
            }

        val command = SetLotsStatusUnsuccessfulCommand(
            cpid = cpid,
            ocid = ocid,
            startDate = startDate,
            lots = lots
        )

        return setLotsStatusUnsuccessfulService.set(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
