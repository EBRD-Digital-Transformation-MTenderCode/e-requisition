package com.procurement.requisition.infrastructure.handler.v1.check

import com.procurement.requisition.infrastructure.handler.v1.check.lot.model.CheckLotsStatusRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckLotsStatusRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckLotsStatusRequest>("json/infrastructure/handler/v1/check/request_check_lots_status.json")
    }
}
