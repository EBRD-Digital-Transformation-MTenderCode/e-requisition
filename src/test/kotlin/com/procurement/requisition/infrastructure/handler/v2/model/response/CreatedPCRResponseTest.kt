package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreatedPCRResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreatedPCRResponse>("json/infrastructure/handler/v2/model/response/response_create_pcr_full.json")
    }
}
