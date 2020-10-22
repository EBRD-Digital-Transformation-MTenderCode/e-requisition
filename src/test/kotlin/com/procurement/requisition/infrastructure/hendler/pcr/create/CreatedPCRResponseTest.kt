package com.procurement.requisition.infrastructure.hendler.pcr.create

import com.procurement.requisition.infrastructure.handler.pcr.create.model.CreatedPCRResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreatedPCRResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreatedPCRResponse>("json/infrastructure/handler/pcr/create/response_create_pcr_full.json")
    }
}
