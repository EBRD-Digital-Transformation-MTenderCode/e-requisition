package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindProcurementMethodModalitiesRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindProcurementMethodModalitiesRequest>("json/infrastructure/handler/v2/model/request/request_find_procurement_method_modalities_full.json")
    }
}
