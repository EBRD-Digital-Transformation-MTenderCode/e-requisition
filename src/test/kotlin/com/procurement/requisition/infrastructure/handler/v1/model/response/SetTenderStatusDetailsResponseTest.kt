package com.procurement.requisition.infrastructure.handler.v1.model.response

import com.procurement.requisition.infrastructure.handler.v1.model.response.SetTenderStatusDetailsResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetTenderStatusDetailsResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetTenderStatusDetailsResponse>("json/infrastructure/handler/v1/set/response_set_tender_status_details.json")
    }
}
