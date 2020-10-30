package com.procurement.requisition.infrastructure.handler.v1.get.lot.auction.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.model.Amount
import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.lot.LotId

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetLotsAuctionResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: TenderId,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
            @field:JsonProperty("value") @param:JsonProperty("value") val value: Value
        ) {
            data class Value(
                @field:JsonProperty("id") @param:JsonProperty("id") val amount: Amount?,
                @field:JsonProperty("id") @param:JsonProperty("id") val currency: String
            )
        }
    }
}