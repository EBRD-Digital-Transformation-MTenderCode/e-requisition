package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetTenderCurrencyRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetTenderCurrencyRequest>("json/infrastructure/handler/v2/model/request/request_get_tender_currency_full.json")
    }
}
