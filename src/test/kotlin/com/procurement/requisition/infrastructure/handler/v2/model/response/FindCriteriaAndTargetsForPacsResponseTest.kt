package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class FindCriteriaAndTargetsForPacsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<FindCriteriaAndTargetsForPacsResponse>("json/infrastructure/handler/v2/model/response/response_find_criteria_and_targets_for_pacs_full.json")
    }
}
