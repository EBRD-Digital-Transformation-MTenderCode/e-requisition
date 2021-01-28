package com.procurement.requisition.infrastructure.handler.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.GetCriteriaForTendererService
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.model.command.GetCriteriaForTendererCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.v1.base.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.model.response.GetCriteriaForTendererResponse
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap

@CommandHandler
class GetCriteriaForTendererHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getCriteriaForTendererService: GetCriteriaForTendererService
) : AbstractHandlerV1() {

    override val action: Action = Actions.GET_CRITERIA_FOR_TENDERER

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid
            .onFailure { return it }

        val ocid = context.ocid
            .onFailure { return it }

        val command = GetCriteriaForTendererCommand(cpid = cpid, ocid = ocid)

        return getCriteriaForTendererService.get(command)
            .flatMap { result ->
                ApiResponseV1.Success(
                    version = descriptor.version, id = descriptor.id, result = GetCriteriaForTendererResponse.fromResult(result)
                )
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
