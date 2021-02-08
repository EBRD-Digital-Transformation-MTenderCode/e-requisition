package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetUnsuccessfulStateForLotsRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetUnsuccessfulStateForLotsRequest>("json/infrastructure/handler/v2/model/request/request_set_unsuccessful_state_for_lots_full.json")
    }
}
