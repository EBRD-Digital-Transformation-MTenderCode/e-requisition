package com.procurement.requisition.infrastructure.web.v2

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
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

fun CommandDescriptor.Companion.v2(content: String, transform: Transform): Result<CommandDescriptor, RequestErrors> =
    CommandsV2.parse(content, transform)
        .mapFailure { failure ->
            RequestErrors(
                code = failure.code,
                underlying = failure.description,
                body = content,
                path = failure.path,
                reason = failure.reason
            )
        }

object CommandsV2 {

    enum class CommandType(override val key: String, override val kind: Action.Kind) :
        EnumElementProvider.Element, Action {

        CHECK_LOTS_STATE("checkLotsState", kind = Action.Kind.QUERY),
        CHECK_TENDER_STATE("checkTenderState", kind = Action.Kind.QUERY),
        CREATE_PCR(key = "createPcr", kind = Action.Kind.COMMAND),
        CREATE_RELATION_TO_CONTRACT_PROCESS_STAGE("createRelationToContractProcessStage", kind = Action.Kind.COMMAND),
        FIND_ITEMS_BY_LOT_IDS("findItemsByLotIds", kind = Action.Kind.QUERY),
        GET_TENDER_STATE("getTenderState", kind = Action.Kind.QUERY),
        VALIDATE_PCR_DATA("validatePcrData", kind = Action.Kind.QUERY);

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
    fun JsonNode.getAction(): Result<CommandType, JsonErrors> =
        tryGetAttributeAsEnum("action", CommandType)

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

    fun getParams(node: JsonNode): Result<JsonNode, JsonErrors> = node.tryGetAttribute("params")
}
