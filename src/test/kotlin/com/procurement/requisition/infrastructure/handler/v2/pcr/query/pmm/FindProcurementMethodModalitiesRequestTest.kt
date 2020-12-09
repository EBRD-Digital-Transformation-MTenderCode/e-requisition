package com.procurement.requisition.infrastructure.handler.v2.pcr.query.pmm

import com.procurement.requisition.infrastructure.handler.v2.model.request.FindProcurementMethodModalitiesRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindProcurementMethodModalitiesRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindProcurementMethodModalitiesRequest>("json/infrastructure/handler/v2/pcr/query/pmm/request_find_procurement_method_modalities_full.json")
    }
}
