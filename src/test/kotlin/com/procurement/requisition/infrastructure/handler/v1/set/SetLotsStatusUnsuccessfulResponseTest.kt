package com.procurement.requisition.infrastructure.handler.v1.set

import com.procurement.requisition.infrastructure.handler.v1.model.response.SetLotsStatusUnsuccessfulResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetLotsStatusUnsuccessfulResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetLotsStatusUnsuccessfulResponse>("json/infrastructure/handler/v1/set/response_set_lots_status_unsuccessful.json")
    }
}
