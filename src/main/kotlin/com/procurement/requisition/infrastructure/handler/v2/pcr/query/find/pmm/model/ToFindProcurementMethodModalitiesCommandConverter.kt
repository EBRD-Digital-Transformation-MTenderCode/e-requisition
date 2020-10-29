package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.pmm.model

import com.procurement.requisition.application.service.find.pmm.model.FindProcurementMethodModalitiesCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
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
    val cpid = cpid.asCpid(path = "#/params/cpid").onFailure { return it }
    val ocid = ocid.asSingleStageOcid(path = "#/params/ocid").onFailure { return it }

    val tender = this.tender.convert("#tender")
        .onFailure { return it }

    return FindProcurementMethodModalitiesCommand(cpid = cpid, ocid = ocid, tender = tender)
        .asSuccess()
}

fun FindProcurementMethodModalitiesRequest.Tender.convert(path: String): Result<FindProcurementMethodModalitiesCommand.Tender, JsonErrors> {
    val procurementMethodModalities = this.procurementMethodModalities
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/procurementMethodModalities")) }
        .mapIndexed { idx, pmm ->
            pmm.asEnum(
                target = ProcurementMethodModality,
                path = "$pmm/procurementMethodModalities[$idx]",
                allowedElements = allowedProcurementMethodModalities
            )
                .onFailure { fail -> return fail }
        }

    return FindProcurementMethodModalitiesCommand.Tender(procurementMethodModalities = procurementMethodModalities)
        .asSuccess()
}
