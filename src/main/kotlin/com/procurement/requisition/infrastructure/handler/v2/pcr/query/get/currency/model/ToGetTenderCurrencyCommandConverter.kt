package com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.currency.model

import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

fun GetTenderCurrencyRequest.convert(): Result<GetTenderCurrencyCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    return GetTenderCurrencyCommand(cpid = cpid, ocid = ocid).asSuccess()
}
