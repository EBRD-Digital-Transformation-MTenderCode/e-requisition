package com.procurement.requisition.infrastructure.handler.v2

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.CheckTenderStateService
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v2.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.requisition.infrastructure.handler.v2.model.request.CheckTenderStateRequest
import com.procurement.requisition.infrastructure.handler.v2.model.request.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure

@CommandHandler
class CheckTenderStateHandler(
    override val logger: Logger,
    override val transform: Transform,
    val checkTenderStateService: CheckTenderStateService
) : AbstractHandlerV2() {

    override val action: Action = Actions.CHECK_TENDER_STATE

    override fun execute(descriptor: CommandDescriptor): Result<String?, Failure> {

        val command = descriptor.getCommand(CheckTenderStateRequest::convert)
            .onFailure { failure -> return failure }

        checkTenderStateService.check(command)
            .onFailure { return failure(it.reason) }

        return ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }
}
