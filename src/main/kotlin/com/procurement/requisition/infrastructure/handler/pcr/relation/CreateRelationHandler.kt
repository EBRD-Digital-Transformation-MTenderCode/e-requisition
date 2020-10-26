package com.procurement.requisition.infrastructure.handler.pcr.relation

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.relation.CreateRelationService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.pcr.relation.model.CreateRelationRequest
import com.procurement.requisition.infrastructure.handler.pcr.relation.model.convert
import com.procurement.requisition.infrastructure.web.v2.CommandsV2
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class CreateRelationHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val createRelationService: CreateRelationService
) : AbstractHandler() {

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val params = CommandsV2.getParams(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }
            .tryMapping<CreateRelationRequest>(transform)
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
            .mapFailure { failure ->
                RequestErrors(
                    code = failure.code,
                    version = descriptor.version,
                    id = descriptor.id,
                    body = descriptor.body.asString,
                    underlying = failure.description,
                    path = failure.path,
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }

        return createRelationService.create(params)
            .flatMap { result ->
                ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
