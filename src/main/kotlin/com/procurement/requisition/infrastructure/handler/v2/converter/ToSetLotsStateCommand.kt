package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.set.model.SetLotsStateCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.SetLotsStateRequest
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun SetLotsStateRequest.convert(): Result<SetLotsStateCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    val pmd = pmd
        .asEnum(target = ProcurementMethodDetails)
        .onFailure { return it.repath(path = "/pmd") }
    val operationType = operationType
        .asEnum(target = OperationType)
        .onFailure { return it.repath(path = "/operationType") }
    val tender = tender.convert()
        .onFailure { return it.repath(path = "/tender") }

    return SetLotsStateCommand(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType,
        tender = tender
    ).asSuccess()
}

fun SetLotsStateRequest.Tender.convert(): Result<SetLotsStateCommand.Tender, JsonErrors> {
    val lots = lots
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "lots")) }
        .mapIndexedOrEmpty { idx, lot ->
            lot.convert().onFailure { return it.repath("/lots[$idx]") }
        }
    return SetLotsStateCommand.Tender(lots = lots).asSuccess()
}

fun SetLotsStateRequest.Tender.Lot.convert(): Result<SetLotsStateCommand.Tender.Lot, JsonErrors> {
    val id = id.asLotId().onFailure { return it.repath("/id") }
    return SetLotsStateCommand.Tender.Lot(id = id).asSuccess()
}
