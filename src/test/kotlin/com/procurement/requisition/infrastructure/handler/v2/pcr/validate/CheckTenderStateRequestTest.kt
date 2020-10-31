package com.procurement.requisition.infrastructure.handler.v2.pcr.validate

import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.model.CheckTenderStateRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckTenderStateRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckTenderStateRequest>("json/infrastructure/handler/v2/pcr/validate/request_check_tender_state_full.json")
    }
}
