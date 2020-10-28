package com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.model

import com.procurement.requisition.application.service.find.items.model.FindItemsByLotIdsCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess

fun FindItemsByLotIdsRequest.convert(): Result<FindItemsByLotIdsCommand, JsonErrors> {
    val cpid = Cpid.tryCreateOrNull(cpid)
        ?: return failure(
            JsonErrors.DataFormatMismatch(path = "#/cpid", actualValue = cpid, expectedFormat = Cpid.pattern)
        )

    val ocid = Ocid.SingleStage.tryCreateOrNull(ocid)
        ?: return failure(
            JsonErrors.DataFormatMismatch(path = "#/ocid", actualValue = ocid, expectedFormat = Cpid.pattern)
        )

    return FindItemsByLotIdsCommand(
        cpid = cpid,
        ocid = ocid,
        tender = this.tender.let { tender ->
            FindItemsByLotIdsCommand.Tender(
                lots = tender.lots.map { lot ->
                    LotId.orNull(lot.id)
                        ?: return failure(
                            JsonErrors.DataFormatMismatch(
                                path = "#/tender/lot",
                                actualValue = lot.id,
                                expectedFormat = LotId.pattern
                            )
                        )
                }
            )
        }
    ).asSuccess()
}
