package com.procurement.requisition.infrastructure.api.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.lib.toList

@JsonPropertyOrder("version", "id")
sealed class ApiResponseV1 {
    abstract val version: ApiVersion
    abstract val id: CommandId

    companion object {
        fun buildError(id: CommandId, version: ApiVersion, failure: com.procurement.requisition.lib.fail.Failure) =
            Failure(
                id = id,
                version = version,
                error = Failure.Error(code = failure.code, description = failure.description).toList()
            )
    }

    class Success(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("data") @param:JsonProperty("data") val result: Any? = null
    ) : ApiResponseV1()

    class Failure(
        @field:JsonProperty("version") @param:JsonProperty("version") override val version: ApiVersion,
        @field:JsonProperty("id") @param:JsonProperty("id") override val id: CommandId,
        @field:JsonProperty("error") @param:JsonProperty("error") val error: List<Error>
    ) : ApiResponseV1() {

        class Error(
            @field:JsonProperty("code") @param:JsonProperty("code") val code: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        )
    }
}

