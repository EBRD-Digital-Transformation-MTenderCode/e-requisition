package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.currency.model

import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

fun GetTenderCurrencyRequest.convert(): Result<GetTenderCurrencyCommand, JsonErrors> {
    val cpid = cpid.asCpid(path = "#/params/cpid").onFailure { return it }
    val ocid = ocid.asSingleStageOcid(path = "#/params/ocid").onFailure { return it }
    return GetTenderCurrencyCommand(cpid = cpid, ocid = ocid).asSuccess()
}
