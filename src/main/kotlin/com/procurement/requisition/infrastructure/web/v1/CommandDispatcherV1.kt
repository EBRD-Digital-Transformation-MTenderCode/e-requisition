package com.procurement.requisition.infrastructure.web.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.infrastructure.handler.Handlers
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.infrastructure.web.AbstractCommandDispatcher
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command")
class CommandDispatcherV1(
    logger: Logger,
    transform: Transform,
    historyRepository: HistoryRepository,
    handlers: Handlers
) : AbstractCommandDispatcher(logger, transform, historyRepository, handlers) {

    override val apiVersion: ApiVersion = ApiVersion(1, 0, 0)

    @PostMapping(produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun command(@RequestBody body: String): ResponseEntity<Any> = dispatch(body)

    override fun JsonNode.getAction(): Result<Action, JsonErrors> = tryGetAttributeAsEnum("command", Actions)

    override fun buildErrorResponse(id: CommandId, version: ApiVersion, failure: Failure): String {
        failure.logging(logger)
        return ApiResponseV1.buildError(id, version, failure)
            .trySerialization(transform)
            .recovery {
                it.logging(logger)
                "Internal Server Error"
            }
    }
}
