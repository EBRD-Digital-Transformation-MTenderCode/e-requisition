package com.procurement.requisition.infrastructure.handler.v1.get

import com.procurement.requisition.infrastructure.handler.v1.model.response.GetTenderCurrencyV1Response
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetTenderCurrencyV1ResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetTenderCurrencyV1Response>("json/infrastructure/handler/v1/get/response_get_tender_currency_v1_full.json")
    }
}
