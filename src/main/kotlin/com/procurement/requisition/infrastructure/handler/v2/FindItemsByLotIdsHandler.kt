package com.procurement.requisition.infrastructure.handler.v2

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.find.items.FindItemsByLotIdsService
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v2.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.requisition.infrastructure.handler.v2.model.request.FindItemsByLotIdsRequest
import com.procurement.requisition.infrastructure.handler.v2.converter.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class FindItemsByLotIdsHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val findItemsByLotIdsService: FindItemsByLotIdsService
) : AbstractHandlerV2() {

    override val action: Action = Actions.FIND_ITEMS_BY_LOT_IDS

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = descriptor.getCommand(FindItemsByLotIdsRequest::convert)
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
