package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetTenderCurrencyResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetTenderCurrencyResponse>("json/infrastructure/handler/v2/model/response/response_get_tender_currency_full.json")
    }
}
