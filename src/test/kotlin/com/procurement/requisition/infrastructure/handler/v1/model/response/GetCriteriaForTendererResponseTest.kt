package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetCriteriaForTendererResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetCriteriaForTendererResponse>("json/infrastructure/handler/v1/get/response_get_criteria_for_tenderer_full.json")
    }
}
