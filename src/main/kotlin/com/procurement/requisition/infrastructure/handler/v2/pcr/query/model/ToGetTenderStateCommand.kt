package com.procurement.requisition.infrastructure.handler.v2.pcr.query.model

import com.procurement.requisition.application.service.get.tender.state.model.GetTenderStateCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess

fun GetTenderStateRequest.convert(): Result<GetTenderStateCommand, JsonErrors> {
    val cpid = Cpid.tryCreateOrNull(cpid)
        ?: return failure(
            JsonErrors.DataFormatMismatch(path = "#/cpid", actualValue = cpid, expectedFormat = Cpid.pattern)
        )

    val ocid = Ocid.SingleStage.tryCreateOrNull(ocid)
        ?: return failure(
            JsonErrors.DataFormatMismatch(path = "#/ocid", actualValue = ocid, expectedFormat = Cpid.pattern)
        )

    return GetTenderStateCommand(cpid = cpid, ocid = ocid).asSuccess()
}
