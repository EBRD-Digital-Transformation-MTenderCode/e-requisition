package com.procurement.requisition.infrastructure.hendler.pcr.validate

import com.procurement.requisition.infrastructure.handler.pcr.validate.ValidatePCRDataParams
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class ValidatePCRDataParamsTest {

    @Test
    fun fully() {
        testingBindingAndMapping<ValidatePCRDataParams>("json/infrastructure/web/dto/pcr_full.json")
    }

    @Test
    fun required1() {
        testingBindingAndMapping<ValidatePCRDataParams>("json/infrastructure/web/dto/pcr_required_1.json")
    }

    @Test
    fun required2() {
        testingBindingAndMapping<ValidatePCRDataParams>("json/infrastructure/web/dto/pcr_required_2.json")
    }
}
