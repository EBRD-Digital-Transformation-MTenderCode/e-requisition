package com.procurement.requisition.infrastructure.repository.pcr.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.extension.asString
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.Documents
import com.procurement.requisition.domain.model.tender.ProcurementMethodModalities
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.Tender
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
import com.procurement.requisition.infrastructure.handler.converter.asTenderId
import com.procurement.requisition.infrastructure.repository.pcr.model.ClassificationEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.DocumentEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.ValueEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToDomain
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToEntity
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

fun Tender.mappingToEntity() = TenderEntity(
    id = id.underlying,
    title = title,
    status = status.asString(),
    statusDetails = statusDetails.asString(),
    date = date.asString(),
    description = description,
    classification = classification.mappingToEntity(),
    lots = lots.map { it.serialization() },
    items = items.map { it.mappingToEntity() },
    targets = targets.map { it.serialization() },
    criteria = criteria.map { it.serialization() },
    conversions = conversions.map { it.serialization() },
    procurementMethodModalities = procurementMethodModalities.map { it.asString() },
    awardCriteria = awardCriteria.asString(),
    awardCriteriaDetails = awardCriteriaDetails.asString(),
    documents = documents.map { it.mappingToEntity() },
    value = value.mappingToEntity()
)

fun TenderEntity.mappingToDomain(): Result<Tender, JsonErrors> {
    val id = id.asTenderId().onFailure { return it.repath(path = "/id") }
    val status = status.asEnum(target = TenderStatus)
        .onFailure { return it.repath(path = "/status") }
    val statusDetails = statusDetails.asEnum(target = TenderStatusDetails)
        .onFailure { return it.repath(path = "/statusDetails") }
    val date = date.asLocalDateTime().onFailure { return it.repath(path = "/date") }
    val classification = classification.mappingToDomain()
        .onFailure { return it.repath(path = "/classification") }
    val lots = lots
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "lots")) }
        .mapIndexedOrEmpty { idx, lot -> lot.deserialization().onFailure { return it.repath(path = "/lots[$idx]") } }
        .let { Lots(it) }
    val items = items
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.mappingToDomain().onFailure { return it.repath(path = "/items[$idx]") }
        }
        .let { Items(it) }
    val targets = targets
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "targets")) }
        .mapIndexedOrEmpty { idx, target ->
            target.deserialization().onFailure { return it.repath(path = "/targets[$idx]") }
        }
        .let { Targets(it) }
    val criteria = criteria
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "criteria")) }
        .mapIndexedOrEmpty { idx, criterion ->
            criterion.deserialization().onFailure { return it.repath(path = "/criteria[$idx]") }
        }
        .let { Criteria(it) }
    val conversions = conversions
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "conversions")) }
        .mapIndexedOrEmpty { idx, conversion ->
            conversion.deserialization().onFailure { return it.repath(path = "/conversions[$idx]") }
        }
        .let { Conversions(it) }
    val procurementMethodModalities = procurementMethodModalities
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(target = ProcurementMethodModality)
                .onFailure { return it.repath(path = "/procurementMethodModalities[$idx]") }
        }
        .let { ProcurementMethodModalities(it) }
    val awardCriteria = awardCriteria.asEnum(target = AwardCriteria)
        .onFailure { return it.repath(path = "/awardCriteria") }
    val awardCriteriaDetails = awardCriteriaDetails.asEnum(target = AwardCriteriaDetails)
        .onFailure { return it.repath(path = "/awardCriteriaDetails") }
    val documents = documents
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "documents")) }
        .mapIndexedOrEmpty { idx, document ->
            document.mappingToDomain().onFailure { return it.repath(path = "/documents[$idx]") }
        }
        .let { Documents(it) }
    val value = value.mappingToDomain().onFailure { return it.repath(path = "/id") }

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
