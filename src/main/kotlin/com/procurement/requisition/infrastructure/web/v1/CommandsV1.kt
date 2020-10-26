package com.procurement.requisition.infrastructure.web.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.requisition.infrastructure.extension.tryGetTextAttribute
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.CommandId
import com.procurement.requisition.infrastructure.handler.v1.CommandContext
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

fun CommandDescriptor.Companion.v1(content: String, transform: Transform): Result<CommandDescriptor, RequestErrors> =
    CommandsV1.parse(content, transform)
        .mapFailure { failure ->
            RequestErrors(
                code = failure.code,
                underlying = failure.description,
                body = content,
                path = failure.path,
                reason = failure.reason
            )
        }

object CommandsV1 {

    enum class CommandType(override val key: String, override val kind: Action.Kind) :
        EnumElementProvider.Element, Action {

        CREATE_REQUESTS_FOR_EV_PANELS(key = "createRequestsForEvPanels", kind = Action.Kind.COMMAND),
        GET_ACTIVE_LOTS(key = "getActiveLots", kind = Action.Kind.QUERY);

        override fun toString(): String = key

        companion object : EnumElementProvider<CommandType>(info = info())
    }

    fun parse(content: String, transform: Transform): Result<CommandDescriptor, JsonErrors> =
        transform.tryParse(content)
            .mapFailure { failure -> JsonErrors.Parsing(reason = failure.reason) }
            .onFailure { failure -> return failure }
            .let { node ->
                CommandDescriptor(
                    version = node.getVersion().onFailure { failure -> return failure },
                    id = node.getId().onFailure { failure -> return failure },
                    action = node.getAction().onFailure { failure -> return failure },
                    body = CommandDescriptor.Body(asString = content, asJsonNode = node)
                )
            }
            .asSuccess()

    fun JsonNode.getId(): Result<CommandId, JsonErrors> = tryGetTextAttribute("id")
    fun JsonNode.getAction(): Result<CommandType, JsonErrors> = tryGetAttributeAsEnum("command", CommandType)
    fun JsonNode.getVersion(): Result<ApiVersion, JsonErrors> = tryGetTextAttribute("version")
        .flatMap { version ->
            ApiVersion.orNull(version)
                ?.asSuccess()
                ?: Result.failure(
                    JsonErrors.DataFormatMismatch(
                        path = "version",
                        actualValue = version,
                        expectedFormat = ApiVersion.pattern,
                        reason = null
                    )
                )
        }

    fun getContext(node: JsonNode): Result<CommandContext, JsonErrors> = node.tryGetAttribute("context")
        .map { CommandContext(it) }
}
