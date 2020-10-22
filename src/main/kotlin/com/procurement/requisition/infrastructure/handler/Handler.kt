package com.procurement.requisition.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.requisition.infrastructure.extension.tryGetTextAttribute
import com.procurement.requisition.infrastructure.handler.model.CommandType
import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandId
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

interface Handler {
    fun handle(request: ApiRequest): String
}

fun parseRequestBody(body: String, transform: Transform): Result<ApiRequest, RequestErrors> {
    val node = transform.tryParse(body)
        .mapFailure { failure ->
            RequestErrors(
                code = failure.code,
                underlying = "Error parsing.",
                body = failure.value,
                reason = failure.reason
            )
        }
        .onFailure { failure -> return failure }

    val version = node.getVersion()
        .mapFailure { failure ->
            RequestErrors(
                code = failure.code,
                underlying = failure.description,
                body = body,
                path = failure.path,
                reason = failure.reason
            )
        }
        .onFailure { failure -> return failure }

    val id = node.getId()
        .mapFailure { failure ->
            RequestErrors(
                code = failure.code,
                underlying = failure.description,
                version = version,
                body = body,
                path = failure.path,
                reason = failure.reason
            )
        }
        .onFailure { failure -> return failure }

    val action = node.getAction()
        .mapFailure { failure ->
            RequestErrors(
                code = failure.code,
                underlying = failure.description,
                version = version,
                id = id,
                body = body,
                path = failure.path,
                reason = failure.reason
            )
        }
        .onFailure { failure -> return failure }

    return ApiRequest(body = body, version = version, id = id, action = action, node = node).asSuccess()
}

fun JsonNode.getId(): Result<CommandId, JsonErrors> = tryGetTextAttribute("id")
fun JsonNode.getAction(): Result<CommandType, JsonErrors> = tryGetAttributeAsEnum("action", CommandType)
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

fun JsonNode.getParams(): Result<JsonNode, JsonErrors> = tryGetAttribute("params")
