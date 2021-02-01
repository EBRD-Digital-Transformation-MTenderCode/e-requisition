package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetUnsuccessfulStateForLotsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetUnsuccessfulStateForLotsResponse>("json/infrastructure/handler/v2/model/response/response_set_unsuccessful_state_for_lots_full.json")
    }
}
