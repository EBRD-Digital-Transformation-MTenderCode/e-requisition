package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class CheckLotAwardedErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckLotAwardedErrors(
            code = "400.${GlobalProperties.service.id}.00.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    sealed class Lot(code: String, description: String) :
        CheckLotAwardedErrors(code = code, description = description) {

        class NotFound(id: LotId) : Lot(
            code = "400.${GlobalProperties.service.id}.10.20",
            description = "Lot by id '${id.underlying}' is not found."
        )

        class InvalidState(status: LotStatus, statusDetails: LotStatusDetails?) : Lot(
            code = "400.${GlobalProperties.service.id}.10.19",
            description = "Invalid state: (status='$status', statusDetails='$statusDetails') of specified lot."
        )
    }
}
