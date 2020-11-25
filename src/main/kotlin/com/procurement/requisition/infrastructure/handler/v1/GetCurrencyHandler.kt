package com.procurement.requisition.infrastructure.handler.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.get.tender.currency.GetTenderCurrencyService
import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.base.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.model.response.GetTenderCurrencyV1Response
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class GetCurrencyHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getTenderCurrencyService: GetTenderCurrencyService
) : AbstractHandlerV1() {

    override val action: Action = Actions.GET_CURRENCY

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid.onFailure { return it }
        val ocid = context.ocid.onFailure { return it }

        val command = GetTenderCurrencyCommand(cpid = cpid, ocid = ocid)

        return getTenderCurrencyService.get(command)
            .flatMap { result ->
                val response = GetTenderCurrencyV1Response(
                    value = GetTenderCurrencyV1Response.Value(
                        currency = result.tender.value.currency
                    )
                )
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = response)
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}