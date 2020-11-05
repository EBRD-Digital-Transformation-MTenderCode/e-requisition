package com.procurement.requisition.infrastructure.handler.v1.check.lot.status

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.check.lot.status.CheckLotsStatusService
import com.procurement.requisition.application.service.check.lot.status.model.CheckLotsStatusCommand
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.check.lot.status.model.CheckLotsStatusRequest
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess

@CommandHandler
class CheckLotsStatusHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val checkLotsStatusService: CheckLotsStatusService
) : AbstractHandlerV1() {

    override val action: Action = Actions.CHECK_LOTS_STATUS

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = buildCommand(descriptor).onFailure { return it }

        checkLotsStatusService.check(command)
            .onFailure { return it.reason.asFailure() }

        return ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }

    fun buildCommand(descriptor: CommandDescriptor): Result<CheckLotsStatusCommand, Failure> {
        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid.onFailure { return it }
        val ocid = context.ocid.onFailure { return it }

        val data = getData<CheckLotsStatusRequest>(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val relatedLot = data.relatedLot.asLotId().onFailure { return it.repath(path = "/relatedLot") }
        return CheckLotsStatusCommand(cpid = cpid, ocid = ocid, relatedLot = relatedLot).asSuccess()
    }
}
