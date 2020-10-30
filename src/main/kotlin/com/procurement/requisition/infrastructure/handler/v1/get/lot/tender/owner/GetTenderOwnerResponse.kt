package com.procurement.requisition.infrastructure.handler.v1.get.lot.tender.owner

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.get.lot.model.ActiveLotIds

data class GetTenderOwnerResponse(
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
)
