package com.procurement.requisition.infrastructure.web.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.extension.nowDefaultUTC
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.Handler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.handler.model.CommandId
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.HandlersV1
import com.procurement.requisition.infrastructure.service.HistoryEntity
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command")
class CommandDispatcherV1(
    private val transform: Transform,
    private val logger: Logger,
    private val handlers: HandlersV1,
    private val historyRepository: HistoryRepository
) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun command(@RequestBody body: String): ResponseEntity<Any> {
        logger.info("RECEIVED COMMAND: '${body}'.")

        val descriptor = CommandDescriptor.v1(body, transform)
            .onFailure { failure ->
                val response = buildErrorResponse(failure.reason.id, failure.reason.version, failure.reason)
                return ResponseEntity(response, HttpStatus.OK)
            }

        val handler = handlers[descriptor.action]
            ?: run {
                val message = "Handler for command '${descriptor.action}' version api 'v1' is not found."
                logger.info(message = message)
                return ResponseEntity(message, HttpStatus.BAD_REQUEST)
            }

        val response = handler.handling(descriptor)
            .recovery { failure -> buildErrorResponse(descriptor.id, descriptor.version, failure) }
            .also { response -> logger.info("RESPONSE (id: '${descriptor.id}'): '$response'.") }

        return ResponseEntity(response, HttpStatus.OK)
    }

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

    fun load(command: CommandDescriptor): Result<String?, Failure> = historyRepository.getHistory(command.id)
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

    fun buildErrorResponse(id: CommandId, version: ApiVersion, failure: Failure): String {
        failure.logging(logger)
        return ApiResponseV1.buildError(id, version, failure)
            .trySerialization(transform)
            .recovery {
                it.logging(logger)
                "Internal Server Error"
            }
    }
}
