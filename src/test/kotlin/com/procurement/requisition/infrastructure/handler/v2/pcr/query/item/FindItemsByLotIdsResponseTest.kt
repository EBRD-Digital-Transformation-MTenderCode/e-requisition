package com.procurement.requisition.infrastructure.handler.v2.pcr.query.item

import com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.model.FindItemsByLotIdsResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindItemsByLotIdsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindItemsByLotIdsResponse>("json/infrastructure/handler/v2/pcr/query/item/response_find_items_by_lot_ids_full.json")
    }
}
