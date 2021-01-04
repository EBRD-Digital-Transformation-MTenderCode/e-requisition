package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckTenderStateRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckTenderStateRequest>("json/infrastructure/handler/v2/model/request/request_check_tender_state_full.json")
    }
}
