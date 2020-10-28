package com.procurement.requisition.infrastructure.repository.pcr.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.Documents
import com.procurement.requisition.domain.model.tender.ProcurementMethodModalities
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.Tender
import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.conversion.Conversions
import com.procurement.requisition.domain.model.tender.criterion.Criteria
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.domain.model.tender.target.Targets
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.repository.pcr.model.ClassificationEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.DocumentEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.ValueEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.serialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.conversion.ConversionEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.conversion.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.conversion.serialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.CriterionEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.serialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.lot.LotEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.lot.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.lot.serialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.TargetEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.serialization
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class TenderEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: String,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: ClassificationEntity,

    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<LotEntity>,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("items") @param:JsonProperty("items") val items: List<ItemEntity>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("targets") @param:JsonProperty("targets") val targets: List<TargetEntity>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("criteria") @param:JsonProperty("criteria") val criteria: List<CriterionEntity>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("conversions") @param:JsonProperty("conversions") val conversions: List<ConversionEntity>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<String>?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteria") @param:JsonProperty("awardCriteria") val awardCriteria: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("awardCriteriaDetails") @param:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<DocumentEntity>?,

    @field:JsonProperty("value") @param:JsonProperty("value") val value: ValueEntity
)

fun Tender.serialization() = TenderEntity(
    id = id.underlying,
    title = title,
    status = status.asString(),
    statusDetails = statusDetails.asString(),
    date = date.asString(),
    description = description,
    classification = classification.serialization(),
    lots = lots.map { it.serialization() },
    items = items.map { it.serialization() },
    targets = targets.map { it.serialization() },
    criteria = criteria.map { it.serialization() },
    conversions = conversions.map { it.serialization() },
    procurementMethodModalities = procurementMethodModalities.map { it.asString() },
    awardCriteria = awardCriteria.asString(),
    awardCriteriaDetails = awardCriteriaDetails.asString(),
    documents = documents.map { it.serialization() },
    value = value.serialization()
)

fun TenderEntity.deserialization(path: String): Result<Tender, JsonErrors> {
    val id = TenderId.orNull(id)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = TenderId.pattern,
                reason = null
            )
        )
    val status = status.asEnum(target = TenderStatus, path = "$path/status")
        .onFailure { return it }
    val statusDetails = statusDetails.asEnum(target = TenderStatusDetails, path = "$path/statusDetails")
        .onFailure { return it }
    val date = date.asLocalDateTime(path = "$path/date").onFailure { return it }
    val classification = classification.deserialization(path = "$path/classification")
        .onFailure { return it }
    val lots = lots
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/lots")) }
        .mapIndexedOrEmpty { idx, lot -> lot.deserialization(path = "$path/lots[$idx]").onFailure { return it } }
        .let { Lots(it) }
    val items = items
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.deserialization(path = "$path/items[$idx]").onFailure { return it }
        }
        .let { Items(it) }
    val targets = targets
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/targets")) }
        .mapIndexedOrEmpty { idx, target ->
            target.deserialization(path = "$path/targets[$idx]").onFailure { return it }
        }
        .let { Targets(it) }
    val criteria = criteria
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/criteria")) }
        .mapIndexedOrEmpty { idx, criterion ->
            criterion.deserialization(path = "$path/criteria[$idx]").onFailure { return it }
        }
        .let { Criteria(it) }
    val conversions = conversions
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/conversions")) }
        .mapIndexedOrEmpty { idx, conversion ->
            conversion.deserialization(path = "$path/conversions[$idx]").onFailure { return it }
        }
        .let { Conversions(it) }
    val procurementMethodModalities = procurementMethodModalities
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(
                target = ProcurementMethodModality,
                path = "$path/procurementMethodModalities[$idx]"
            )
                .onFailure { return it }
        }
        .let { ProcurementMethodModalities(it) }
    val awardCriteria = awardCriteria.asEnum(target = AwardCriteria, path = "$path/awardCriteria")
        .onFailure { return it }
    val awardCriteriaDetails =
        awardCriteriaDetails.asEnum(target = AwardCriteriaDetails, path = "$path/awardCriteriaDetails")
            .onFailure { return it }
    val documents = documents
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/documents")) }
        .mapIndexedOrEmpty { idx, document ->
            document.deserialization(path = "$path/documents[$idx]").onFailure { return it }
        }
        .let { Documents(it) }
    val value = value.deserialization(path = "$path/id")
        .onFailure { return it }

    return Tender(
        id = id,
        status = status,
        statusDetails = statusDetails,
        title = title,
        description = description,
        date = date,
        classification = classification,
        lots = lots,
        items = items,
        targets = targets,
        criteria = criteria,
        conversions = conversions,
        procurementMethodModalities = procurementMethodModalities,
        awardCriteria = awardCriteria,
        awardCriteriaDetails = awardCriteriaDetails,
        documents = documents,
        value = value
    ).asSuccess()
}
