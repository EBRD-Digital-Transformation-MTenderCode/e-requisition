package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.document.Document
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.RelatedLots
import com.procurement.requisition.infrastructure.handler.converter.asDocumentId
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class DocumentEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String,

    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>?
)

fun Document.mappingToEntity() = DocumentEntity(
    id = id.underlying,
    documentType = documentType.asString(),
    title = title,
    description = description,
    relatedLots = relatedLots.map { it.underlying }
)

fun DocumentEntity.mappingToDomain(path: String): Result<Document, JsonErrors> {
    val id = id.asDocumentId(path = "$path/id").onFailure { return it }
    val documentType = documentType.asEnum(target = DocumentType, path = "$path/documentType")
        .onFailure { return it }
    val relatedLots = relatedLots
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/relatedLots")) }
        .mapIndexedOrEmpty { idx, relatedLot ->
            LotId.orNull(relatedLot)
                ?: return Result.failure(
                    JsonErrors.DataFormatMismatch(
                        path = "$path/relatedLots[$idx]",
                        actualValue = relatedLot,
                        expectedFormat = LotId.pattern,
                        reason = null
                    )
                )
        }
        .let { t -> RelatedLots(t) }

    return Document(
        id = id,
        documentType = documentType,
        title = title,
        description = description,
        relatedLots = relatedLots
    ).asSuccess()
}
