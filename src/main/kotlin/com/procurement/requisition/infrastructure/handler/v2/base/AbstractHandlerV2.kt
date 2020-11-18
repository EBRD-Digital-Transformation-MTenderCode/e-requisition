package com.procurement.requisition.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.handler.base.AbstractHandler
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHandlerV2 : AbstractHandler() {

    companion object {
        const val PARAMS_ATTRIBUTE_NAME = "params"
    }

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    inline fun <reified T, R> CommandDescriptor.getCommand(
        crossinline converter: (T) -> Result<R, JsonErrors>
    ): Result<R, JsonErrors> = body.asJsonNode
        .tryGetAttribute(PARAMS_ATTRIBUTE_NAME)
        .flatMap { node -> node.mapping<T>() }
        .flatMap { converter(it) }
        .repath(path = "/$PARAMS_ATTRIBUTE_NAME")

    inline fun <reified T> JsonNode.mapping(): Result<T, JsonErrors> = transform.tryMapping(this, T::class.java)
        .mapFailure { failure ->
            JsonErrors.Parsing(reason = failure.reason)
        }
}
