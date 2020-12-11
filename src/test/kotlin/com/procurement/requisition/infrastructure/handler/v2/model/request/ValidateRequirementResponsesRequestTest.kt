package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class ValidateRequirementResponsesRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<ValidateRequirementResponsesRequest>("json/infrastructure/handler/v2/model/request/request_validate_requirement_responses_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<ValidateRequirementResponsesRequest>("json/infrastructure/handler/v2/model/request/request_validate_requirement_responses_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<ValidateRequirementResponsesRequest>("json/infrastructure/handler/v2/model/request/request_validate_requirement_responses_required_2.json")
    }
}
