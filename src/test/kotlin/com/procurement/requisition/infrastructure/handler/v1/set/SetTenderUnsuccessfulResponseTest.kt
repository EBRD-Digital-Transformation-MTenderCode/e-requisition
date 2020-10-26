package com.procurement.requisition.infrastructure.handler.v1.set

import com.procurement.requisition.infrastructure.handler.v1.set.model.SetTenderUnsuccessfulResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetTenderUnsuccessfulResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetTenderUnsuccessfulResponse>("json/infrastructure/handler/v1/set/response_set_tender_unsuccessful.json")
    }
}
