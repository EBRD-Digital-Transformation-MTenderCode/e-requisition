package com.procurement.requisition.infrastructure.handler.v2.pcr.relation.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.relation.model.CreateRelationResult
import com.procurement.requisition.infrastructure.handler.converter.asString

data class CreatedRelationResponse(
    @field:JsonProperty("relatedProcesses") @param:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcess>
) {

    data class RelatedProcess(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
        @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: String,
        @field:JsonProperty("relationship") @param:JsonProperty("relationship") val relationship: List<String>,
        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
    )
}

fun CreateRelationResult.convert() = CreatedRelationResponse(
    relatedProcesses = relatedProcesses.map { it.convert() }
)

fun CreateRelationResult.RelatedProcess.convert() = CreatedRelationResponse.RelatedProcess(
    id = id.underlying,
    scheme = scheme.asString(),
    identifier = identifier,
    relationship = relationship.map { it.asString() },
    uri = uri
)
