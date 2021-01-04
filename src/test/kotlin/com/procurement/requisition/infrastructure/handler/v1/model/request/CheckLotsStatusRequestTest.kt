package com.procurement.requisition.infrastructure.handler.v1.model.request

import com.procurement.requisition.infrastructure.handler.v1.model.request.CheckLotsStatusRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CheckLotsStatusRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CheckLotsStatusRequest>("json/infrastructure/handler/v1/check/request_check_lots_status.json")
    }
}
