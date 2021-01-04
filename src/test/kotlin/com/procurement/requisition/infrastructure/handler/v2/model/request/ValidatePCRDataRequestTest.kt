package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class ValidatePCRDataRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<ValidatePCRDataRequest>("json/infrastructure/handler/v2/model/request/request_validate_pcr_data_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<ValidatePCRDataRequest>("json/infrastructure/handler/v2/model/request/request_validate_pcr_data_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<ValidatePCRDataRequest>("json/infrastructure/handler/v2/model/request/request_validate_pcr_data_required_2.json")
    }
}
