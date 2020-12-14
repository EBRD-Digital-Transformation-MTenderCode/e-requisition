package com.procurement.requisition.infrastructure.handler.v2

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.ValidateRequirementResponsesService
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v2.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.requisition.infrastructure.handler.v2.model.request.ValidateRequirementResponsesRequest
import com.procurement.requisition.infrastructure.handler.v2.converter.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure

@CommandHandler
class ValidateRequirementResponsesHandler(
    override val logger: Logger,
    override val transform: Transform,
    val validateRequirementResponsesService: ValidateRequirementResponsesService
) : AbstractHandlerV2() {

    override val action: Action = Actions.VALIDATE_REQUIREMENT_RESPONSES

    override fun execute(descriptor: CommandDescriptor): Result<String?, Failure> {

        val command = descriptor.getCommand(ValidateRequirementResponsesRequest::convert)
            .onFailure { failure -> return failure }

        validateRequirementResponsesService.validate(command)
            .onFailure { return failure(it.reason) }

        return ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }
}
