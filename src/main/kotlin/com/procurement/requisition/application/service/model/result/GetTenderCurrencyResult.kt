package com.procurement.requisition.application.service.model.result

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
