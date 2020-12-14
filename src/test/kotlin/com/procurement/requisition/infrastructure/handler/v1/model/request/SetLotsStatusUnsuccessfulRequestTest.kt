package com.procurement.requisition.infrastructure.handler.v1.model.request

import com.procurement.requisition.infrastructure.handler.v1.model.request.SetLotsStatusUnsuccessfulRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetLotsStatusUnsuccessfulRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetLotsStatusUnsuccessfulRequest>("json/infrastructure/handler/v1/set/request_lots_status_unsuccessful_full.json")
    }
}
