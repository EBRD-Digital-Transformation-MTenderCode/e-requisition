package com.procurement.requisition.infrastructure.handler.v1.set.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.set.model.SetLotsStatusUnsuccessfulCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.tender.lot.LotId
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
    val id = LotId.orNull(id)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = DocumentId.pattern,
                reason = null
            )
        )

    return SetLotsStatusUnsuccessfulCommand.Lot(id = id).asSuccess()
}
