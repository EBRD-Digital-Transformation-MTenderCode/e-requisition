package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.find.items.FindItemsByLotIdsService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.model.FindItemsByLotIdsRequest
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.model.convert
import com.procurement.requisition.infrastructure.web.v2.CommandsV2
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class FindItemsByLotIdsHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val findItemsByLotIdsService: FindItemsByLotIdsService
) : AbstractHandler() {

    override val version: ApiVersion = CommandsV2.apiVersion
    override val action: Action = CommandsV2.CommandType.FIND_ITEMS_BY_LOT_IDS

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = CommandsV2.getParams(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }
            .tryMapping<FindItemsByLotIdsRequest>(transform)
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

        return findItemsByLotIdsService.find(command)
            .flatMap { result ->
                val response =
                    if (result.tender.items.isEmpty())
                        null
                    else
                        result.convert()

                ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = response)
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
