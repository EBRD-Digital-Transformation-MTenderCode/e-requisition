package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.command.FindCriteriaAndTargetsForPacsCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.FindCriteriaAndTargetsForPacsRequest
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun FindCriteriaAndTargetsForPacsRequest.convert(): Result<FindCriteriaAndTargetsForPacsCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }

    val tender = tender.convert()
        .onFailure { return it.repath(path = "/tender") }

    return FindCriteriaAndTargetsForPacsCommand(
        cpid = cpid,
        ocid = ocid,
        tender = tender
    ).asSuccess()
}

fun FindCriteriaAndTargetsForPacsRequest.Tender.convert(): Result<FindCriteriaAndTargetsForPacsCommand.Tender, JsonErrors> {
    val lots = lots
        .failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "lots")) }
        .mapIndexedOrEmpty { idx, lot ->
            lot.convert().onFailure { return it.repath("/lots[$idx]") }
        }
    return FindCriteriaAndTargetsForPacsCommand.Tender(lots = lots).asSuccess()
}

fun FindCriteriaAndTargetsForPacsRequest.Tender.Lot.convert(): Result<FindCriteriaAndTargetsForPacsCommand.Tender.Lot, JsonErrors> {
    val id = id.asLotId().onFailure { return it.repath("/id") }
    return FindCriteriaAndTargetsForPacsCommand.Tender.Lot(id = id).asSuccess()
}
