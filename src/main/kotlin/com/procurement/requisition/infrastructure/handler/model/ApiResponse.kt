package com.procurement.requisition.infrastructure.handler.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.toList
import java.time.LocalDateTime
import java.util.*

@JsonPropertyOrder("version", "id", "status", "result")
sealed class ApiResponse {
    abstract val version: ApiVersion
    abstract val id: CommandId
    abstract val status: ResponseStatus
    abstract val result: Any?

    class Success(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: Any? = null
    ) : ApiResponse() {

        @field:JsonProperty("status")
        override val status: ResponseStatus = ResponseStatus.SUCCESS
    }

    class Error(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: List<Error>
    ) : ApiResponse() {

        @field:JsonProperty("status")
        override val status: ResponseStatus = ResponseStatus.ERROR

        class Error(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail> = emptyList()
        ) {

            class Detail private constructor(
                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String? = null,

                @field:JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String? = null
            ) {

                companion object {
                    fun tryCreateOrNull(id: String? = null, name: String? = null): Detail? =
                        if (id == null && name == null) null else Detail(id = id, name = name)
                }
            }
        }
    }

    class Incident(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: Incident
    ) : ApiResponse() {

        @field:JsonProperty("status")
        override val status: ResponseStatus = ResponseStatus.INCIDENT

        class Incident(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: IncidentId,
            @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
            @field:JsonProperty("level") @param:JsonProperty("level") val level: Failure.Incident.Level,
            @field:JsonProperty("service") @param:JsonProperty("service") val service: Service,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("details") @param:JsonProperty("details") val details: List<Detail>
        ) {

            class Service(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                @field:JsonProperty("version") @param:JsonProperty("version") val version: String
            )

            class Detail(
                @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("metadata") @param:JsonProperty("metadata") val metadata: Any?
            )
        }
    }
}

fun errorResponse(logger: Logger, failure: Failure, id: CommandId, version: ApiVersion): ApiResponse {
    failure.logging(logger)
    return when (failure) {
        is RequestErrors -> generateRequestErrorResponse(id = id, version = version, error = failure)
        is Failure.Error -> generateErrorResponse(id = id, version = version, error = failure)
        is Failure.Incident -> generateIncidentResponse(id = id, version = version, incident = failure)
    }
}

fun generateErrorResponse(id: CommandId, version: ApiVersion, error: Failure.Error) =
    ApiResponse.Error(
        version = version,
        id = id,
        result = listOf(
            ApiResponse.Error.Error(
                code = "${error.code}/${GlobalProperties.service.id}",
                description = error.description
            )
        )
    )

fun generateIncidentResponse(
    id: CommandId,
    version: ApiVersion,
    incident: Failure.Incident
) = ApiResponse.Incident(
    id = id,
    version = version,
    result = ApiResponse.Incident.Incident(
        id = UUID.randomUUID().toString(),
        date = LocalDateTime.now(),
        level = incident.level,
        details = listOf(
            ApiResponse.Incident.Incident.Detail(
                code = "${incident.code}/${GlobalProperties.service.id}",
                description = incident.description,
                metadata = null
            )
        ),
        service = ApiResponse.Incident.Incident.Service(
            id = GlobalProperties.service.id,
            version = GlobalProperties.service.version,
            name = GlobalProperties.service.name
        )
    )
)

fun generateRequestErrorResponse(id: CommandId, version: ApiVersion, error: RequestErrors) = ApiResponse.Error(
    version = version,
    id = id,
    result = listOf(
        ApiResponse.Error.Error(
            code = "${error.code}/${GlobalProperties.service.id}",
            description = error.description,
            details = ApiResponse.Error.Error.Detail.tryCreateOrNull(name = error.path).toList()
        )
    )
)

/*fun generateValidationErrorResponse(id: CommandId, version: ApiVersion, fail: ValidationErrors) = ApiResponse.Error(
    version = version,
    id = id,
    result = listOf(
        ApiResponse.Error.Error(
            code = "${fail.code}/${GlobalProperties.service.id}",
            description = fail.description,
            details = ApiResponse.Error.Error.Detail.tryCreateOrNull(id = fail.entityId).toList()
        )
    )
)*/
