package com.procurement.requisition.infrastructure.handler.v1.get

import com.procurement.requisition.infrastructure.handler.v1.model.response.GetAwardCriteriaAndConversionsResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetAwardCriteriaAndConversionsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetAwardCriteriaAndConversionsResponse>("json/infrastructure/handler/v1/get/response_get_award_criteria_and_conversions.json")
    }
}
