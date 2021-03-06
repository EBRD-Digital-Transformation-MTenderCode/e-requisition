package com.procurement.requisition.infrastructure.repository.pcr.model.tender

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.infrastructure.bind.quantity.QuantityDeserializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantitySerializer
import com.procurement.requisition.infrastructure.handler.converter.asItemId
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.repository.pcr.model.ClassificationEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.UnitEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToDomain
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToEntity
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import java.math.BigDecimal

data class ItemEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

    @JsonDeserialize(using = QuantityDeserializer::class)
    @JsonSerialize(using = QuantitySerializer::class)
    @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,

    @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: ClassificationEntity,

    @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: UnitEntity,

    @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String
)

fun Item.mappingToEntity() = ItemEntity(
    id = id.underlying,
    internalId = internalId,
    description = description,
    quantity = quantity,
    classification = classification.mappingToEntity(),
    unit = unit.mappingToEntity(),
    relatedLot = relatedLot.underlying,
)

fun ItemEntity.mappingToDomain(): Result<Item, JsonErrors> {
    val id = id.asItemId().onFailure { return it.repath(path = "/id") }
    val classification = classification.mappingToDomain().onFailure { return it.repath(path = "/classification") }
    val unit = unit.mappingToDomain().onFailure { return it.repath(path = "/unit") }
    val relatedLot = relatedLot.asLotId().onFailure { return it.repath(path = "/relatedLot") }

    return Item(
        id = id,
        internalId = internalId,
        description = description,
        quantity = quantity,
        classification = classification,
        unit = unit,
        relatedLot = relatedLot,
    ).asSuccess()
}
