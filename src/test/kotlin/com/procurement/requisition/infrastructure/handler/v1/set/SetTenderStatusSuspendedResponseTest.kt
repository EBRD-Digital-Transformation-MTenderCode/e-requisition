package com.procurement.requisition.infrastructure.handler.v1.set

import com.procurement.requisition.infrastructure.handler.v1.model.response.SetTenderStatusSuspendedResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetTenderStatusSuspendedResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetTenderStatusSuspendedResponse>("json/infrastructure/handler/v1/set/response_set_tender_status_suspended.json")
    }
}
