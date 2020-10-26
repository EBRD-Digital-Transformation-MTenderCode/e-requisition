package com.procurement.requisition.infrastructure.handler.pcr.validate

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.validate.ValidatePCRService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.pcr.validate.model.ValidatePCRDataRequest
import com.procurement.requisition.infrastructure.handler.pcr.validate.model.convert
import com.procurement.requisition.infrastructure.web.api.CommandsV2
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.Result.Companion.success

class ValidatePCRDataHandler(
    override val logger: Logger,
    override val transform: Transform,
    val validatePCRService: ValidatePCRService
) : AbstractHandler() {

    override fun execute(descriptor: CommandDescriptor): Result<String?, Failure> {

        val params = CommandsV2.getParams(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }
            .tryMapping<ValidatePCRDataRequest>(transform)
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

        validatePCRService.validate(params)
            .onFailure { return failure(it.reason) }

        return success(null)
    }
}
