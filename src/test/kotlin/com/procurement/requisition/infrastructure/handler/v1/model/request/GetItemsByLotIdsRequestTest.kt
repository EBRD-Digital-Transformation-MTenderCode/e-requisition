package com.procurement.requisition.infrastructure.handler.v1.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetItemsByLotIdsRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetItemsByLotIdsRequest>("json/infrastructure/handler/v1/get/request_get_items_by_lot_ids_full.json")
    }
}
