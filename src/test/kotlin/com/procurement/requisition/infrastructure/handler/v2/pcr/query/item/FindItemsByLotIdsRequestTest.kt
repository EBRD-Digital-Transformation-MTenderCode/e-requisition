package com.procurement.requisition.infrastructure.handler.v2.pcr.query.item

import com.procurement.requisition.infrastructure.handler.v2.model.request.FindItemsByLotIdsRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindItemsByLotIdsRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindItemsByLotIdsRequest>("json/infrastructure/handler/v2/pcr/query/item/request_find_items_by_lot_ids_full.json")
    }
}
