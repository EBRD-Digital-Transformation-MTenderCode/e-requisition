package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindCriteriaAndTargetsForPacsRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindCriteriaAndTargetsForPacsRequest>("json/infrastructure/handler/v2/model/request/request_find_criteria_and_targets_for_pacs_full.json")
    }
}
