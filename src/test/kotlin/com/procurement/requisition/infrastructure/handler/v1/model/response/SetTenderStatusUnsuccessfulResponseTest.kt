package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.procurement.requisition.infrastructure.handler.v1.model.response.SetTenderStatusUnsuccessfulResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetTenderStatusUnsuccessfulResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetTenderStatusUnsuccessfulResponse>("json/infrastructure/handler/v1/set/response_set_tender_status_unsuccessful.json")
    }
}
