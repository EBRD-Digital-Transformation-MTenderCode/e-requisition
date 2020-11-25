package com.procurement.requisition.infrastructure.web

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.extension.nowDefaultUTC
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.extension.tryGetTextAttribute
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.base.Handler
import com.procurement.requisition.infrastructure.handler.Handlers
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.infrastructure.service.HistoryEntity
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class AbstractCommandDispatcher(
    val logger: Logger,
    val transform: Transform,
    private val historyRepository: HistoryRepository,
    private val handlers: Handlers
) {

    abstract val apiVersion: ApiVersion

    abstract fun JsonNode.getAction(): Result<Action, JsonErrors>

    abstract fun buildErrorResponse(id: CommandId, version: ApiVersion, failure: Failure): String

    fun dispatch(body: String): ResponseEntity<Any> {
        logger.info("RECEIVED COMMAND: '${body}'.")

        val descriptor = parse(body, transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = failure.code,
                    description = failure.description,
                    body = body,
                    path = failure.path.asString(),
                    reason = failure.reason
                )
            }
            .onFailure { failure ->
                val response = buildErrorResponse(id = CommandId.NaN, version = apiVersion, failure = failure.reason)
                return ResponseEntity(response, HttpStatus.OK)
            }

        val handler = handlers[apiVersion, descriptor.action]
            ?: run {
                val message =
                    "Handler for command '${descriptor.action}' version api '${apiVersion.underlying}' is not found."
                logger.info("ERROR RESPONSE (id: '${descriptor.id}'): '$message'.")
                return ResponseEntity(message, HttpStatus.BAD_REQUEST)
            }

        val response = handler.handling(descriptor)
            .recovery { failure -> buildErrorResponse(descriptor.id, descriptor.version, failure) }
            .also { response -> logger.info("RESPONSE (id: '${descriptor.id}'): '$response'.") }

        return ResponseEntity(response, HttpStatus.OK)
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

    fun JsonNode.getVersion(): Result<ApiVersion, JsonErrors> = tryGetTextAttribute("version")
        .flatMap { version ->
            ApiVersion.orNull(version)
                ?.asSuccess()
                ?: Result.failure(
                    JsonErrors.DataFormatMismatch(actualValue = version, expectedFormat = ApiVersion.pattern)
                        .repath(path = "/version")
                )
        }

    fun JsonNode.getId(): Result<CommandId, JsonErrors> = tryGetTextAttribute("id").map { CommandId(it) }

    fun Handler.handling(descriptor: CommandDescriptor): Result<String?, Failure> {
        if (descriptor.action.kind == Action.Kind.COMMAND) {
            val history = load(descriptor)
                .onFailure { return it }
            if (history != null) return history.asSuccess()
        }

        val result = handle(descriptor)
            .onFailure { return it }

        if (descriptor.action.kind == Action.Kind.COMMAND && result != null) {
            save(descriptor, result)
        }
        return result.asSuccess()
    }

    fun load(command: CommandDescriptor): Result<String?, Failure> = historyRepository.getHistory(command.id, command.action)
        .onFailure { return it }
        .asSuccess()

    fun save(command: CommandDescriptor, result: String) {
        val newHistory = HistoryEntity(
            commandId = command.id,
            action = command.action,
            date = nowDefaultUTC(),
            data = result
        )
        historyRepository.saveHistory(newHistory)
            .doOnError { failure -> failure.logging(logger) }
    }
}
