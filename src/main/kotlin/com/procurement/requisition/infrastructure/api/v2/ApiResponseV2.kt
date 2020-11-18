package com.procurement.requisition.infrastructure.api.v2

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.lib.fail.Failure
import java.time.LocalDateTime

@JsonPropertyOrder("version", "id", "status", "result")
sealed class ApiResponseV2 {
    abstract val version: ApiVersion
    abstract val id: CommandId
    abstract val status: Status
    abstract val result: Any?

    class Success(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: Any? = null
    ) : ApiResponseV2() {

        @field:JsonProperty("status")
        override val status: Status = Status.SUCCESS
    }

    class Error(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,
        @field:JsonProperty("result") @param:JsonProperty("result") override val result: List<Error>
    ) : ApiResponseV2() {

        @field:JsonProperty("status")
        override val status: Status = Status.ERROR

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
    ) : ApiResponseV2() {

        @field:JsonProperty("status")
        override val status: Status = Status.INCIDENT

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

    enum class Status(@JsonValue override val key: String) : EnumElementProvider.Element {
        SUCCESS("success"),
        ERROR("error"),
        INCIDENT("incident");

        override fun toString(): String = key

        companion object : EnumElementProvider<Status>(info = info()) {

            @JvmStatic
            @JsonCreator
            fun creator(name: String) = Status.orThrow(name)
        }
    }
}
