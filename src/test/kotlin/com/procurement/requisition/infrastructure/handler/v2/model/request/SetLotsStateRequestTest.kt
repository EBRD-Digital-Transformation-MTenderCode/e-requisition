package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetLotsStateRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetLotsStateRequest>("json/infrastructure/handler/v2/model/request/request_set_lots_state_full.json")
    }
}
