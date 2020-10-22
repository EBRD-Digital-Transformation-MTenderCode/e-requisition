package com.procurement.requisition.infrastructure.hendler.pcr.validate

import com.procurement.requisition.infrastructure.handler.pcr.validate.model.ValidatePCRDataParams
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class ValidatePCRDataParamsTest {

    @Test
    fun fully() {
        testingBindingAndMapping<ValidatePCRDataParams>("json/infrastructure/handler/pcr/validate/request_validate_pcr_data_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<ValidatePCRDataParams>("json/infrastructure/handler/pcr/validate/request_validate_pcr_data_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<ValidatePCRDataParams>("json/infrastructure/handler/pcr/validate/request_validate_pcr_data_required_2.json")
    }
}
