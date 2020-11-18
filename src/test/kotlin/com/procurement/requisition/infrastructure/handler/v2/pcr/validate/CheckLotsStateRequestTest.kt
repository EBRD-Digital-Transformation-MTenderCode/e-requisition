package com.procurement.requisition.infrastructure.handler.v2.pcr.validate

import com.procurement.requisition.infrastructure.handler.v2.model.request.CheckLotsStateRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckLotsStateRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckLotsStateRequest>("json/infrastructure/handler/v2/pcr/validate/request_check_lots_state_full.json")
    }
}
