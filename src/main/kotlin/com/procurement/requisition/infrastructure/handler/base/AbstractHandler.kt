package com.procurement.requisition.infrastructure.handler.base

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHandler : Handler {

    abstract val transform: Transform
    abstract val logger: Logger

    override fun handle(descriptor: CommandDescriptor): Result<String?, Failure> = execute(descriptor)
        .mapFailure { failure ->
            if (failure is JsonErrors)
                RequestErrors(
                    code = failure.code,
                    body = descriptor.body.asString,
                    description = failure.description,
                    path = failure.path.asString(),
                    reason = failure.reason
                )
            else
                failure
        }

    abstract fun execute(descriptor: CommandDescriptor): Result<String?, Failure>
}
