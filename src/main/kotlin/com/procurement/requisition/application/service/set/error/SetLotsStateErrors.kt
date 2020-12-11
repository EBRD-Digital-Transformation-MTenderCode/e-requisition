package com.procurement.requisition.application.service.set.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure

sealed class SetLotsStateErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        SetLotsStateErrors(
            code = "VR.COM-17.12.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class UnknownLot(lotIds: Collection<LotId>) :
        SetLotsStateErrors(
            code = "VR.COM-17.12.2",
            description = "Unknown lot(s) '${lotIds.joinToString { it.underlying }}'."
        )
}
