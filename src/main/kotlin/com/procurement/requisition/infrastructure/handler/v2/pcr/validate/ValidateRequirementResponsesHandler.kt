package com.procurement.requisition.infrastructure.handler.v2.pcr.validate

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.validate.ValidateRequirementResponsesService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.validate.model.ValidateRequirementResponsesRequest
import com.procurement.requisition.infrastructure.handler.v2.validate.model.convert
import com.procurement.requisition.infrastructure.web.v2.CommandsV2
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure

@CommandHandler
class ValidateRequirementResponsesHandler(
    override val logger: Logger,
    override val transform: Transform,
    val validateRequirementResponsesService: ValidateRequirementResponsesService
) : AbstractHandler() {

    override val version: ApiVersion = ApiVersion(2, 0, 0)
    override val action: Action = CommandsV2.CommandType.VALIDATE_REQUIREMENT_RESPONSES

    override fun execute(descriptor: CommandDescriptor): Result<String?, Failure> {

        val params = CommandsV2.getParams(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }
            .tryMapping<ValidateRequirementResponsesRequest>(transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = "RQ-1",
                    version = descriptor.version,
                    id = descriptor.id,
                    body = descriptor.body.asString,
                    underlying = failure.description,
                    path = "params",
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }
            .convert()
            .repath(path = "/params")
            .mapFailure { failure ->
                RequestErrors(
                    code = failure.code,
                    version = descriptor.version,
                    id = descriptor.id,
                    body = descriptor.body.asString,
                    underlying = failure.description,
                    path = failure.path.asString(),
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }

        validateRequirementResponsesService.validate(params)
            .onFailure { return failure(it.reason) }

        return ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }
}
