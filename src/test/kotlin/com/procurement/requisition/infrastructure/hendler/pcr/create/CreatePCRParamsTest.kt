package com.procurement.requisition.infrastructure.hendler.pcr.create

import com.procurement.requisition.infrastructure.handler.pcr.create.model.CreatePCRParams
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class CreatePCRParamsTest {

    @Test
    fun fully() {
        testingBindingAndMapping<CreatePCRParams>("json/infrastructure/handler/pcr/create/request_create_pcr_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<CreatePCRParams>("json/infrastructure/handler/pcr/create/request_create_pcr_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<CreatePCRParams>("json/infrastructure/handler/pcr/create/request_create_pcr_required_2.json")
    }
}
