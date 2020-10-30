package com.procurement.requisition.infrastructure.web.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.error.repath
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
                path = failure.path.asString(),
                reason = failure.reason
            )
        }

object CommandsV1 {

    val apiVersion: ApiVersion
        get() = ApiVersion(1, 0, 0)

    enum class CommandType(override val key: String, override val kind: Action.Kind) :
        EnumElementProvider.Element, Action {

        CREATE_REQUESTS_FOR_EV_PANELS(key = "createRequestsForEvPanels", kind = Action.Kind.COMMAND),
        GET_ACTIVE_LOTS(key = "getActiveLots", kind = Action.Kind.QUERY),
        GET_AWARD_CRITERIA_AND_CONVERSIONS(key = "getAwardCriteriaAndConversions", kind = Action.Kind.QUERY),
        GET_TENDER_OWNER(key = "getTenderOwner", kind = Action.Kind.QUERY),
        SET_LOTS_STATUS_UNSUCCESSFUL(key = "setLotsStatusUnsuccessful", kind = Action.Kind.COMMAND),
        SET_TENDER_STATUS_DETAILS(key = "setTenderStatusDetails", kind = Action.Kind.COMMAND),
        SET_TENDER_STATUS_UNSUCCESSFUL(key = "setTenderUnsuccessful", kind = Action.Kind.COMMAND),
        ;

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
                    JsonErrors.DataFormatMismatch(actualValue = version, expectedFormat = ApiVersion.pattern)
                        .repath(path = "/version")
                )
        }

    fun getContext(node: JsonNode): Result<CommandContext, JsonErrors> = node.tryGetAttribute("context")
        .map { CommandContext(it) }

    fun getData(node: JsonNode): Result<JsonNode, JsonErrors> = node.tryGetAttribute("data")
}
