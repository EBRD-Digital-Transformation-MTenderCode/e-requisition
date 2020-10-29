package com.procurement.requisition.infrastructure.handler.v1.set.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.set.model.SetLotsStatusUnsuccessfulCommand
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.lib.fail.Failure
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

fun SetLotsStatusUnsuccessfulRequest.Lot.convert(path: String): Result<SetLotsStatusUnsuccessfulCommand.Lot, Failure> {
    val id = id.asLotId(path = "$path/id").onFailure { return it }
    return SetLotsStatusUnsuccessfulCommand.Lot(id = id).asSuccess()
}
