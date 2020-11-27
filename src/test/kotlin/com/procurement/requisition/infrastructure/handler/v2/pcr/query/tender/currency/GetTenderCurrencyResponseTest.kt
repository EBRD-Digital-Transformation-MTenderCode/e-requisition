package com.procurement.requisition.infrastructure.handler.v2.pcr.query.tender.currency

import com.procurement.requisition.infrastructure.handler.v2.model.response.GetTenderCurrencyResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetTenderCurrencyResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetTenderCurrencyResponse>("json/infrastructure/handler/v2/pcr/query/tender/currency/response_get_tender_currency_full.json")
    }
}
