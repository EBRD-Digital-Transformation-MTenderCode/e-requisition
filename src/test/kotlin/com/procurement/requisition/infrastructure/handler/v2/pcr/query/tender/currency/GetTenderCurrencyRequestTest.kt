package com.procurement.requisition.infrastructure.handler.v2.pcr.query.tender.currency

import com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.currency.model.GetTenderCurrencyRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetTenderCurrencyRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetTenderCurrencyRequest>("json/infrastructure/handler/v2/pcr/query/tender/currency/request_get_tender_currency_full.json")
    }
}
