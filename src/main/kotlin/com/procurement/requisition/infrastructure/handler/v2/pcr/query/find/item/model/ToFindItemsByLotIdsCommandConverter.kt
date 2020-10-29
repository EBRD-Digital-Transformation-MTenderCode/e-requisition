package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.model

import com.procurement.requisition.application.service.find.items.model.FindItemsByLotIdsCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess

fun FindItemsByLotIdsRequest.convert(): Result<FindItemsByLotIdsCommand, JsonErrors> {
    val cpid = cpid.asCpid(path = "#/params/cpid").onFailure { return it }
    val ocid = ocid.asSingleStageOcid(path = "#/params/ocid").onFailure { return it }

    val tender = this.tender.convert("#tender")
        .onFailure { return it }

    return FindItemsByLotIdsCommand(cpid = cpid, ocid = ocid, tender = tender)
        .asSuccess()
}

fun FindItemsByLotIdsRequest.Tender.convert(path: String): Result<FindItemsByLotIdsCommand.Tender, JsonErrors> {

    val lots = this.lots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/lots")) }
        .mapIndexed { idx, lot ->
            LotId.orNull(lot.id)
                ?: return failure(
                    JsonErrors.DataFormatMismatch(
                        path = "$path/lots[$idx]",
                        actualValue = lot.id,
                        expectedFormat = LotId.pattern
                    )
                )
        }

    return FindItemsByLotIdsCommand.Tender(lots = lots)
        .asSuccess()
}
