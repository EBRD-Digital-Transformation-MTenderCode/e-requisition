package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.result.SetUnsuccessfulStateForLotsResult
import com.procurement.requisition.infrastructure.handler.v2.model.response.SetUnsuccessfulStateForLotsResponse

fun SetUnsuccessfulStateForLotsResult.convert() = SetUnsuccessfulStateForLotsResponse(
    tender = SetUnsuccessfulStateForLotsResponse.Tender(
        lots = tender.lots.map { lot ->
            SetUnsuccessfulStateForLotsResponse.Tender.Lot(
                id = lot.id.underlying,
                status = lot.status.toString(),
                statusDetails = lot.statusDetails.toString()
            )
        }
    )
)