package com.procurement.requisition.infrastructure.handler.v2

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHandlerV2 : AbstractHandler() {

    companion object {
        const val PARAMS_ATTRIBUTE_NAME = "params"
    }

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    override fun handle(descriptor: CommandDescriptor): Result<String?, Failure> = execute(descriptor)

    inline fun <reified T, C> CommandDescriptor.getCommand(converter: (T) -> Result<C, JsonErrors>): Result<C, Failure> =
        body.asJsonNode.tryGetAttribute(PARAMS_ATTRIBUTE_NAME)
            .onFailure { failure -> return failure }
            .tryMapping<T>(transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = "RQ-1",
                    version = version,
                    id = id,
                    body = body.asString,
                    underlying = failure.description,
                    path = "/$PARAMS_ATTRIBUTE_NAME",
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }
            .let { converter(it).repath(path = "/$PARAMS_ATTRIBUTE_NAME") }
            .mapFailure { failure ->
                RequestErrors(
                    code = failure.code,
                    version = version,
                    id = id,
                    body = body.asString,
                    underlying = failure.description,
                    path = failure.path.asString(),
                    reason = failure.reason
                )
            }
}
