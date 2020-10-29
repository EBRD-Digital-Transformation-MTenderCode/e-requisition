package com.procurement.requisition.infrastructure.repository.pcr.model.tender.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Variants
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.repository.pcr.model.ClassificationEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToDomain
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToEntity
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class LotEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: ClassificationEntity,
    @field:JsonProperty("variants") @param:JsonProperty("variants") val variants: List<VariantEntity>
)

fun Lot.serialization() = LotEntity(
    id = id.underlying,
    internalId = internalId,
    title = title,
    description = description,
    status = status.asString(),
    statusDetails = statusDetails.asString(),
    classification = classification.mappingToEntity(),
    variants = variants.map { it.serialization() },
)

fun LotEntity.deserialization(path: String): Result<Lot, JsonErrors> {
    val id = id.asLotId(path = "$path/id").onFailure { return it }
    val status = status.asEnum(target = LotStatus, path = "$path/status")
        .onFailure { return it }
    val statusDetails = statusDetails.asEnum(target = LotStatusDetails, path = "$path/statusDetails")
        .onFailure { return it }
    val classification = classification.mappingToDomain(path = "$path/classification").onFailure { return it }
    val variants = variants
        .map { variant ->
            variant.deserialization(path = "$path/variants")
                .onFailure { return it }
        }
        .let { Variants(it) }

    return Lot(
        id = id,
        internalId = internalId,
        title = title,
        description = description,
        status = status,
        statusDetails = statusDetails,
        classification = classification,
        variants = variants,
    ).asSuccess()
}
