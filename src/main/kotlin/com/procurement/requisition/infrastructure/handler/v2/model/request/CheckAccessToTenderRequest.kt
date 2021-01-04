package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.model.command.CheckAccessToTenderCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.converter.asToken
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class CheckAccessToTenderRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("token") @param:JsonProperty("token") val token: String,
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String
)

fun CheckAccessToTenderRequest.convert(): Result<CheckAccessToTenderCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    val token = token.asToken().onFailure { return it.repath(path = "/token") }
    
    return CheckAccessToTenderCommand(
        cpid = cpid,
        ocid = ocid,
        token = token,
        owner = owner
    ).asSuccess()
}
