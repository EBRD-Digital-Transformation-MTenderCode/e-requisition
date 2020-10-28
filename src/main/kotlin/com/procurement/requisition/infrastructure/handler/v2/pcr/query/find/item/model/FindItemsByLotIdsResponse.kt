package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.infrastructure.bind.quantity.QuantityDeserializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantitySerializer
import java.math.BigDecimal

data class FindItemsByLotIdsResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>,
    ) {
        data class Item(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

            @JsonDeserialize(using = QuantityDeserializer::class)
            @JsonSerialize(using = QuantitySerializer::class)
            @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,

            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,

            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String
        )

        data class Classification(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: ClassificationScheme,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        )

        data class Unit(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
        )
    }
}

