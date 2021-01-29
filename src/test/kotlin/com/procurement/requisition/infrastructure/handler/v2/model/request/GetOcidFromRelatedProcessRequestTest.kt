package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetOcidFromRelatedProcessRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetOcidFromRelatedProcessRequest>("json/infrastructure/handler/v2/model/request/request_get_ocid_from_related_process_full.json")
    }
}
