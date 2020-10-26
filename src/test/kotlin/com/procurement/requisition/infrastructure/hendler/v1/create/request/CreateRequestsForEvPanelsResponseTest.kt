package com.procurement.requisition.infrastructure.hendler.v1.create.request

import com.procurement.requisition.infrastructure.handler.v1.create.request.model.CreateRequestsForEvPanelsResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreateRequestsForEvPanelsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreateRequestsForEvPanelsResponse>("json/infrastructure/handler/v1/create/request/response_create_requests_for_ev_panels_full.json")
    }
}
