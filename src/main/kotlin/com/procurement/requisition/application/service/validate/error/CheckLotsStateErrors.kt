package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure

sealed class CheckLotsStateErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckLotsStateErrors(
            code = "VR.COM-17.10.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    sealed class Lot(code: String, description: String) : CheckLotsStateErrors(code = code, description = description) {
        class Unknown(lotId: LotId) :
            Lot(
                code = "VR.COM-17.10.2",
                description = "Lot by id '${lotId.underlying}' is not found."
            )

        class InvalidState(lotId: LotId, status: TenderStatus) :
            Lot(
                code = "VR.COM-17.10.3",
                description = "Lot with id '$lotId' has invalid status '${status.key}'."
            )
    }
}
