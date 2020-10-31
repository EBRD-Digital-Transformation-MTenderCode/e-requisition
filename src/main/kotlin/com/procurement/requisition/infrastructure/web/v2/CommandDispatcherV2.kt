package com.procurement.requisition.infrastructure.web.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.Handlers
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandId
import com.procurement.requisition.infrastructure.handler.model.response.ApiResponseV2
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.infrastructure.web.AbstractCommandDispatcher
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.toList
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/command2")
class CommandDispatcherV2(
    logger: Logger,
    transform: Transform,
    historyRepository: HistoryRepository,
    handlers: Handlers
) : AbstractCommandDispatcher(logger, transform, historyRepository, handlers) {

    override val apiVersion: ApiVersion = ApiVersion(2, 0, 0)

    @PostMapping(produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun command(@RequestBody body: String): ResponseEntity<Any> = dispatch(body)

    override fun JsonNode.getAction(): Result<Action, JsonErrors> =
        tryGetAttributeAsEnum("action", CommandsV2.CommandType)

    override fun buildErrorResponse(id: CommandId, version: ApiVersion, failure: Failure): String {
        failure.logging(logger)
        return when (failure) {
            is RequestErrors -> generateRequestErrorResponse(id = id, version = version, error = failure)
            is Failure.Error -> generateErrorResponse(id = id, version = version, error = failure)
            is Failure.Incident -> generateIncidentResponse(id = id, version = version, incident = failure)
        }
            .trySerialization(transform)
            .recovery {
                it.logging(logger)
                "Internal Server Error"
            }
    }

    fun generateErrorResponse(id: CommandId, version: ApiVersion, error: Failure.Error) =
        ApiResponseV2.Error(
            version = version,
            id = id,
            result = listOf(
                ApiResponseV2.Error.Error(
                    code = "${error.code}/${GlobalProperties.service.id}",
                    description = error.description
                )
            )
        )

    fun generateIncidentResponse(
        id: CommandId,
        version: ApiVersion,
        incident: Failure.Incident
    ) = ApiResponseV2.Incident(
        id = id,
        version = version,
        result = ApiResponseV2.Incident.Incident(
            id = UUID.randomUUID().toString(),
            date = LocalDateTime.now(),
            level = incident.level,
            details = listOf(
                ApiResponseV2.Incident.Incident.Detail(
                    code = "${incident.code}/${GlobalProperties.service.id}",
                    description = incident.description,
                    metadata = null
                )
            ),
            service = ApiResponseV2.Incident.Incident.Service(
                id = GlobalProperties.service.id,
                version = GlobalProperties.service.version,
                name = GlobalProperties.service.name
            )
        )
    )

    fun generateRequestErrorResponse(id: CommandId, version: ApiVersion, error: RequestErrors) = ApiResponseV2.Error(
        version = version,
        id = id,
        result = listOf(
            ApiResponseV2.Error.Error(
                code = "${error.code}/${GlobalProperties.service.id}",
                description = error.description,
                details = ApiResponseV2.Error.Error.Detail.tryCreateOrNull(name = error.path).toList()
            )
        )
    )
}
