package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.model.result.SetTenderStatusUnsuccessfulResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v1.model.response.SetTenderStatusUnsuccessfulResponse

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
