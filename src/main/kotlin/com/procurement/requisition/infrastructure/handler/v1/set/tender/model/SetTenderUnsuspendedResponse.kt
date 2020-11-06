package com.procurement.requisition.infrastructure.handler.v1.set.tender.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality

data class SetTenderUnsuspendedResponse(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String,
        @field:JsonProperty("procurementMethodModalities") @param:JsonProperty("procurementMethodModalities") val procurementMethodModalities: List<ProcurementMethodModality>
    )
}