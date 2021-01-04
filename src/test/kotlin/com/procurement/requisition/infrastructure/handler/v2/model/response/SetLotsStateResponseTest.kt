package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetLotsStateResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetLotsStateResponse>("json/infrastructure/handler/v2/model/response/response_set_lots_state_full.json")
    }
}
