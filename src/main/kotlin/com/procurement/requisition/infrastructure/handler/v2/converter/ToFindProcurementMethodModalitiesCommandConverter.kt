package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.command.FindProcurementMethodModalitiesCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.FindProcurementMethodModalitiesRequest
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess

private val allowedProcurementMethodModalities = ProcurementMethodModality.allowedElements
    .asSequence()
    .filter {
        when (it) {
            ProcurementMethodModality.REQUIRES_ELECTRONIC_CATALOGUE,
            ProcurementMethodModality.ELECTRONIC_AUCTION -> true
        }
    }
    .toSet()

fun FindProcurementMethodModalitiesRequest.convert(): Result<FindProcurementMethodModalitiesCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    val tender = this.tender.convert()
        .onFailure { return it.repath(path = "/tender") }

    return FindProcurementMethodModalitiesCommand(cpid = cpid, ocid = ocid, tender = tender)
        .asSuccess()
}

fun FindProcurementMethodModalitiesRequest.Tender.convert(): Result<FindProcurementMethodModalitiesCommand.Tender, JsonErrors> {
    val procurementMethodModalities = this.procurementMethodModalities
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/procurementMethodModalities")) }
        .mapIndexed { idx, pmm ->
            pmm.asEnum(target = ProcurementMethodModality, allowedElements = allowedProcurementMethodModalities)
                .onFailure { fail -> return fail.repath(path = "$pmm/procurementMethodModalities[$idx]") }
        }

    return FindProcurementMethodModalitiesCommand.Tender(procurementMethodModalities = procurementMethodModalities)
        .asSuccess()
}
