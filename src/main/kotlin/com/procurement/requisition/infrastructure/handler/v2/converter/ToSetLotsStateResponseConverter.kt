package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.set.model.SetLotsStateResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v2.model.response.SetLotsStateResponse

fun SetLotsStateResult.convert() = SetLotsStateResponse(
    tender = tender.convert()
)

fun SetLotsStateResult.Tender.convert() = SetLotsStateResponse.Tender(
    lots = lots.map { lot -> lot.convert() }
)

fun SetLotsStateResult.Tender.Lot.convert() = SetLotsStateResponse.Tender.Lot(
    id = id.underlying,
    status = status.asString(),
    statusDetails = statusDetails.asString()
)
