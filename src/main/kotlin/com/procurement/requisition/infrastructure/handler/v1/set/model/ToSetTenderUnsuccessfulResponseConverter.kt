package com.procurement.requisition.infrastructure.handler.v1.set.model

import com.procurement.requisition.application.service.set.model.SetTenderUnsuccessfulResult
import com.procurement.requisition.infrastructure.handler.converter.asString

fun SetTenderUnsuccessfulResult.convert() = SetTenderUnsuccessfulResponse(
    tender = tender.convert(),
    lots = tender.lots.map { lot -> lot.convert() }
)

fun SetTenderUnsuccessfulResult.Tender.convert() = SetTenderUnsuccessfulResponse.Tender(
    status = status.asString(),
    statusDetails = statusDetails.asString()
)

fun SetTenderUnsuccessfulResult.Tender.Lot.convert() = SetTenderUnsuccessfulResponse.Lot(
    id = id.underlying,
    status = status.asString()
)
