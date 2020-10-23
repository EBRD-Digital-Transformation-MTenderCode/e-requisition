package com.procurement.requisition.infrastructure.web.controller

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.Handlers
import com.procurement.requisition.infrastructure.handler.model.generateRequestErrorResponse
import com.procurement.requisition.infrastructure.handler.parseRequestBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command2")
class Command2Controller(
    private val transform: Transform,
    private val logger: Logger,
    private val handlers: Handlers,
) {

    @PostMapping
    fun command(@RequestBody body: String): ResponseEntity<Any> {
        logger.info("RECEIVED COMMAND: '${body}'.")

        val request = parseRequestBody(body, transform)
            .onFailure { failure -> return badRequest(failure.reason) }

        val handler = handlers[request.version, request.action]
            ?: run {
                val message = "Handler for command '${request.action}' by version '${request.version}' is not found."
                logger.info(message = message)
                return ResponseEntity(message, HttpStatus.BAD_REQUEST)
            }

        val response = handler.handle(request)
            .also { response ->
                logger.info("RESPONSE (id: '${request.id}'): '$response'.")
            }

        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun badRequest(error: RequestErrors): ResponseEntity<Any> {
        error.logging(logger)
        val response = generateRequestErrorResponse(id = error.id, version = error.version, error = error)
        return ResponseEntity.ok(response)
    }
}
