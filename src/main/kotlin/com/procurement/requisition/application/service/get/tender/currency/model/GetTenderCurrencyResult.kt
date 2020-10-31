package com.procurement.requisition.application.service.get.tender.currency.model

data class GetTenderCurrencyResult(
    val tender: Tender
) {
    data class Tender(
        val value: Value
    ) {
        data class Value(
            val currency: String
        )
    }
}
