package com.procurement.requisition.application.service.check.lot.status.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class CheckLotsStatusErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckLotsStatusErrors(
            code = "400.${GlobalProperties.service.id}.00.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    sealed class Lot(code: String, description: String) :
        CheckLotsStatusErrors(code = code, description = description) {

        class NotFound(id: LotId) : Lot(
            code = "400.${GlobalProperties.service.id}.10.20",
            description = "Lot by id '${id.underlying}' is not found."
        )

        class InvalidStatus(id: LotId, status: LotStatus) : Lot(
            code = "400.${GlobalProperties.service.id}.10.19",
            description = "Invalid status '$status' of lot '${id.underlying}'."
        )
    }
}
