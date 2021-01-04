package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.procurement.requisition.infrastructure.handler.v1.model.response.CreateRequestsForEvPanelsResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateRequestsForEvPanelsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateRequestsForEvPanelsResponse>("json/infrastructure/handler/v1/create/request/response_create_requests_for_ev_panels_full.json")
    }
}
