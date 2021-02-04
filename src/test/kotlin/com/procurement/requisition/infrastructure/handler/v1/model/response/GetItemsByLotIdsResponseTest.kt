package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetItemsByLotIdsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetItemsByLotIdsResponse>("json/infrastructure/handler/v1/get/response_get_items_by_lot_ids_full.json")
    }

    @Test
    fun required() {
        testingBindingAndMapping<GetItemsByLotIdsResponse>("json/infrastructure/handler/v1/get/response_get_items_by_lot_ids_requred_1.json")
    }
}
