package com.procurement.requisition.infrastructure.handler.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.GetItemsByLotIdsService
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.model.command.GetItemsByLotIdsCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.v1.base.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.converter.convert
import com.procurement.requisition.infrastructure.handler.v1.model.request.GetItemsByLotIdsRequest
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.functional.flatMap
import com.procurement.requisition.lib.mapIndexedOrEmpty

@CommandHandler
class GetItemsByLotIdsHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getItemsByLotIdsService: GetItemsByLotIdsService
) : AbstractHandlerV1() {

    override val action: Action = Actions.GET_ITEMS_BY_LOT_IDS

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val command = buildCommand(descriptor).onFailure { return it }

        return getItemsByLotIdsService.get(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }

    fun buildCommand(descriptor: CommandDescriptor): Result<GetItemsByLotIdsCommand, Failure> {
        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid.onFailure { return it }
        val ocid = context.ocid.onFailure { return it }

        val data = getData<GetItemsByLotIdsRequest>(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val lots = data.lots
            .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "lots")) }
            .mapIndexedOrEmpty { idx, lot ->
                val id = lot.id.asLotId().onFailure { return it.repath(path = "/lots[$idx]/id") }
                GetItemsByLotIdsCommand.Lot(id)
            }
        return GetItemsByLotIdsCommand(cpid = cpid, ocid = ocid, lots = lots).asSuccess()
    }
}
