package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindProcurementMethodModalitiesResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindProcurementMethodModalitiesResponse>("json/infrastructure/handler/v2/model/response/response_find_procurement_method_modalities_full.json")
    }
}
