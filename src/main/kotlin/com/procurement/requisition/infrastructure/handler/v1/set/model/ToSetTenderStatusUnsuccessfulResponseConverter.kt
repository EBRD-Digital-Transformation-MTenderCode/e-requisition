package com.procurement.requisition.infrastructure.handler.v1.set.model

import com.procurement.requisition.application.service.set.model.SetTenderStatusUnsuccessfulResult
import com.procurement.requisition.infrastructure.handler.converter.asString

fun SetTenderStatusUnsuccessfulResult.convert() = SetTenderStatusUnsuccessfulResponse(
    tender = tender.convert(),
    lots = tender.lots.map { lot -> lot.convert() }
)

fun SetTenderStatusUnsuccessfulResult.Tender.convert() = SetTenderStatusUnsuccessfulResponse.Tender(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)

fun SetTenderStatusUnsuccessfulResult.Tender.Lot.convert() = SetTenderStatusUnsuccessfulResponse.Lot(
    id = id.underlying,
    status = status.asString()
)
