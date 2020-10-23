package com.procurement.requisition.infrastructure.hendler.pcr.create

import com.procurement.requisition.infrastructure.handler.pcr.create.model.CreatePCRRequest
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreatePCRRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreatePCRRequest>("json/infrastructure/handler/pcr/create/request_create_pcr_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreatePCRRequest>("json/infrastructure/handler/pcr/create/request_create_pcr_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<CreatePCRRequest>("json/infrastructure/handler/pcr/create/request_create_pcr_required_2.json")
    }
}
