package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.command.SetUnsuccessfulStateForLotsCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.SetUnsuccessfulStateForLotsRequest
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

fun SetUnsuccessfulStateForLotsRequest.convert(): Result<SetUnsuccessfulStateForLotsCommand, JsonErrors> = SetUnsuccessfulStateForLotsCommand(
    cpid = cpid.asCpid().onFailure { return it },
    ocid = ocid.asSingleStageOcid().onFailure { return it },
    tender = SetUnsuccessfulStateForLotsCommand.Tender(
        lots = tender.lots.map { lot ->
            SetUnsuccessfulStateForLotsCommand.Tender.Lot(lot.id.asLotId().onFailure { return it })
        }
    )
).asSuccess()