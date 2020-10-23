package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcess
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessScheme
import com.procurement.requisition.domain.model.relatedprocesses.Relationship
import com.procurement.requisition.domain.model.relatedprocesses.Relationships
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class RelatedProcessEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("relationship") @param:JsonProperty("relationship") val relationship: List<String>,
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: String,
    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
)

fun RelatedProcess.serialization() = RelatedProcessEntity(
    id = id.underlying,
    scheme = scheme.asString(),
    identifier = identifier,
    relationship = relationship.map { it.asString() },
    uri = uri
)

fun RelatedProcessEntity.deserialization(path: String): Result<RelatedProcess, JsonErrors> {
    val id = RelatedProcessId.orNull(id)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = RelatedProcessId.pattern,
                reason = null
            )
        )
    val scheme = scheme.asEnum(target = RelatedProcessScheme, path = "$path/scheme")
        .onFailure { return it }

    val relationship = relationship
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/relationship")) }
        .mapIndexedOrEmpty { idx, relationship ->
            relationship.asEnum(target = Relationship, path = "$path/relationship[idx]")
                .onFailure { return it }
        }
        .let { t -> Relationships(t) }

    return RelatedProcess(
        id = id,
        scheme = scheme,
        identifier = identifier,
        relationship = relationship,
        uri = uri
    ).asSuccess()
}
