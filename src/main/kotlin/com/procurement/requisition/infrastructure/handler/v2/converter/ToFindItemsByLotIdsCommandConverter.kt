package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.command.FindItemsByLotIdsCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.FindItemsByLotIdsRequest
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess

fun FindItemsByLotIdsRequest.convert(): Result<FindItemsByLotIdsCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    val tender = this.tender.convert()
        .onFailure { return it.repath(path = "/tender") }

    return FindItemsByLotIdsCommand(cpid = cpid, ocid = ocid, tender = tender)
        .asSuccess()
}

fun FindItemsByLotIdsRequest.Tender.convert(): Result<FindItemsByLotIdsCommand.Tender, JsonErrors> {

    val lots = lots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/lots")) }
        .mapIndexed { idx, lot ->
            lot.id.asLotId().onFailure { return it.repath(path = "/lots[$idx]") }
        }

    return FindItemsByLotIdsCommand.Tender(lots = lots)
        .asSuccess()
}
