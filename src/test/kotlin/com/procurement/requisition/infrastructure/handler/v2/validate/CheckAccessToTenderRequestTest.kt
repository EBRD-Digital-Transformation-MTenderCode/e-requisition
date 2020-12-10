package com.procurement.requisition.infrastructure.handler.v2.validate

import com.procurement.requisition.infrastructure.handler.v2.model.request.CheckAccessToTenderRequest
import com.procurement.requisition.infrastructure.handler.v2.model.request.ValidateRequirementResponsesRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckAccessToTenderRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckAccessToTenderRequest>("json/infrastructure/handler/v2/pcr/validate/request_check_access_to_tender_full.json")
    }
}
