package com.procurement.requisition.infrastructure.handler.v2.pcr.validate

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.validate.CheckLotsStateService
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.AbstractHandlerV2
import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.model.CheckLotsStateRequest
import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure

@CommandHandler
class CheckLotsStateHandler(
    override val logger: Logger,
    override val transform: Transform,
    val checkLotsStateService: CheckLotsStateService
) : AbstractHandlerV2() {

    override val action: Action = Actions.CHECK_LOTS_STATE

    override fun execute(descriptor: CommandDescriptor): Result<String?, Failure> {

        val command = descriptor.getCommand(CheckLotsStateRequest::convert)
            .onFailure { failure -> return failure }

        checkLotsStateService.check(command)
            .onFailure { return failure(it.reason) }

        return ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }
}
