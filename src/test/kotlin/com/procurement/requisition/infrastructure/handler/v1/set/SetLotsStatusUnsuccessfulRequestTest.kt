package com.procurement.requisition.infrastructure.handler.v1.set

import com.procurement.requisition.infrastructure.handler.v1.set.model.SetLotsStatusUnsuccessfulRequest
import com.procurement.requisition.infrastructure.handler.v1.set.model.SetLotsStatusUnsuccessfulResponse
import com.procurement.requisition.infrastructure.handler.v1.set.model.SetTenderUnsuccessfulResponse
import com.procurement.requisition.json.testingBindingAndMapping
import org.junit.jupiter.api.Test

class SetLotsStatusUnsuccessfulRequestTest {

    @Test
    fun fully() {
        testingBindingAndMapping<SetLotsStatusUnsuccessfulRequest>("json/infrastructure/handler/v1/set/request_lots_status_unsuccessful_full.json")
    }
}
