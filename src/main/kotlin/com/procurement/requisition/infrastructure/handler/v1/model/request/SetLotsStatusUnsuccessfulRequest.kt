package com.procurement.requisition.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.set.model.SetLotsStatusUnsuccessfulCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

class SetLotsStatusUnsuccessfulRequest(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("unsuccessfulLots") @param:JsonProperty("unsuccessfulLots") val lots: List<Lot>
) {
    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )
}

fun SetLotsStatusUnsuccessfulRequest.Lot.convert(): Result<SetLotsStatusUnsuccessfulCommand.Lot, JsonErrors> {
    val id = id.asLotId().onFailure { return it.repath(path = "/id") }
    return SetLotsStatusUnsuccessfulCommand.Lot(id = id).asSuccess()
}
