package com.procurement.requisition.infrastructure.handler.v2.model.response

import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class GetOcidFromRelatedProcessResponseTest {

    @Test
    fun fully() {
        testingBindingAndMapping<GetOcidFromRelatedProcessResponse>("json/infrastructure/handler/v2/model/response/response_get_ocid_from_related_process_full.json")
    }
}
