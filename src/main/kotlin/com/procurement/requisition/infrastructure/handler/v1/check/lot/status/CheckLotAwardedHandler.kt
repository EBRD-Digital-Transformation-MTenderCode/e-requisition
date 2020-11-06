package com.procurement.requisition.infrastructure.handler.v1.check.lot.status

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.check.lot.status.CheckLotAwardedService
import com.procurement.requisition.application.service.check.lot.status.model.CheckLotAwardedCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.AbstractHandlerV1
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess

@CommandHandler
class CheckLotAwardedHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val checkLotAwardedService: CheckLotAwardedService
) : AbstractHandlerV1() {

    override val action: Action = Actions.CHECK_LOT_AWARDED

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = buildCommand(descriptor).onFailure { return it }

        checkLotAwardedService.check(command)
            .onFailure { return it.reason.asFailure() }

        return ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }

    fun buildCommand(descriptor: CommandDescriptor): Result<CheckLotAwardedCommand, Failure> {
        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid.onFailure { return it }
        val ocid = context.ocid.onFailure { return it }
        val lotId = context.id
            .flatMap { it.asLotId() }
            .onFailure { return it }

        return CheckLotAwardedCommand(cpid = cpid, ocid = ocid, lotId = lotId).asSuccess()
    }
}
