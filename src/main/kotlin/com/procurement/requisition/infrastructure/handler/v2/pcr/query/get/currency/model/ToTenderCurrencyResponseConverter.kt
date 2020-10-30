package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.currency.model

import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyResult

fun GetTenderCurrencyResult.convert(): GetTenderCurrencyResponse =
    GetTenderCurrencyResponse(tender = this.tender.convert())

fun GetTenderCurrencyResult.Tender.convert(): GetTenderCurrencyResponse.Tender =
    GetTenderCurrencyResponse.Tender(value = this.value.convert())

fun GetTenderCurrencyResult.Tender.Value.convert(): GetTenderCurrencyResponse.Tender.Value =
    GetTenderCurrencyResponse.Tender.Value(currency = this.currency)
