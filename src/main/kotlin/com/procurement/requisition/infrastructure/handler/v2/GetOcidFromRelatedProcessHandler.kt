package com.procurement.requisition.infrastructure.handler.v2

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.GetOcidFromRelatedProcessService
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.api.v2.ApiResponseV2
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.requisition.infrastructure.handler.v2.converter.convert
import com.procurement.requisition.infrastructure.handler.v2.model.request.GetOcidFromRelatedProcessRequest
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure

@CommandHandler
class GetOcidFromRelatedProcessHandler(
    override val logger: Logger,
    override val transform: Transform,
    val getOcidFromRelatedProcessService: GetOcidFromRelatedProcessService
) : AbstractHandlerV2() {

    override val action: Action = Actions.GET_OCID_FROM_RELATED_PROCESS

    override fun execute(descriptor: CommandDescriptor): Result<String?, Failure> {

        val command = descriptor.getCommand(GetOcidFromRelatedProcessRequest::convert)
            .onFailure { failure -> return failure }

        getOcidFromRelatedProcessService.get(command)
            .onFailure { return failure(it.reason) }

        return ApiResponseV2.Success(version = descriptor.version, id = descriptor.id, result = null)
            .trySerialization(transform)
            .mapFailure { failure ->
                InternalServerError(description = failure.description, reason = failure.reason)
            }
    }
}
