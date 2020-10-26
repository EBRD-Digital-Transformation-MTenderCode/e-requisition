package com.procurement.requisition.infrastructure.handler.v1.lot

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.get.lot.GetActiveLotsService
import com.procurement.requisition.application.service.get.lot.model.GetActiveLotIdsCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.lot.model.convert
import com.procurement.requisition.infrastructure.web.api.CommandsV1
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

class GetActiveLotsHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getActiveLotsService: GetActiveLotsService
) : AbstractHandler() {

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = CommandsV1.getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid
            .onFailure { return it }

        val ocid = context.ocid
            .onFailure { return it }

        val command = GetActiveLotIdsCommand(cpid = cpid, ocid = ocid)

        return getActiveLotsService.get(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
