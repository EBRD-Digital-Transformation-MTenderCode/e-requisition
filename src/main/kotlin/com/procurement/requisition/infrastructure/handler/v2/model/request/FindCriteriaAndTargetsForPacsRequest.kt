package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class FindCriteriaAndTargetsForPacsRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(
        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
        )
    }
}
