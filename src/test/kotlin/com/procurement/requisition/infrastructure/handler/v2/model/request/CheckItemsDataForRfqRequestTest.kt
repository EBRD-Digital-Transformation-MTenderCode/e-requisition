package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckItemsDataForRfqRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckItemsDataForRfqRequest>("json/infrastructure/handler/v2/model/request/check_items_data_for_rfq_full.json")
    }
}
