package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.result.GetTenderCurrencyResult
import com.procurement.requisition.infrastructure.handler.v2.model.response.GetTenderCurrencyResponse

fun GetTenderCurrencyResult.convert(): GetTenderCurrencyResponse =
    GetTenderCurrencyResponse(tender = this.tender.convert())

fun GetTenderCurrencyResult.Tender.convert(): GetTenderCurrencyResponse.Tender =
    GetTenderCurrencyResponse.Tender(value = this.value.convert())

fun GetTenderCurrencyResult.Tender.Value.convert(): GetTenderCurrencyResponse.Tender.Value =
    GetTenderCurrencyResponse.Tender.Value(currency = this.currency)
