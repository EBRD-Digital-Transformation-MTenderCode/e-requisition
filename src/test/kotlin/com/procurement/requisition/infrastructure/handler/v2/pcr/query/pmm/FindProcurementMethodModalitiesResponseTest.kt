package com.procurement.requisition.infrastructure.handler.v2.pcr.query.pmm

import com.procurement.requisition.infrastructure.handler.v2.model.response.FindProcurementMethodModalitiesResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindProcurementMethodModalitiesResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindProcurementMethodModalitiesResponse>("json/infrastructure/handler/v2/pcr/query/pmm/response_find_procurement_method_modalities_full.json")
    }
}
