package com.procurement.requisition.infrastructure.handler.v1.set.model

import com.procurement.requisition.application.service.set.model.SetLotsStatusUnsuccessfulResult
import com.procurement.requisition.infrastructure.handler.converter.asString

fun SetLotsStatusUnsuccessfulResult.convert() = SetLotsStatusUnsuccessfulResponse(
    tender = tender.convert(),
    lots = tender.lots.map { lot -> lot.convert() }
)

fun SetLotsStatusUnsuccessfulResult.Tender.convert() = SetLotsStatusUnsuccessfulResponse.Tender(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)

fun SetLotsStatusUnsuccessfulResult.Tender.Lot.convert() = SetLotsStatusUnsuccessfulResponse.Lot(
    id = id.underlying,
    status = status.asString()
)
