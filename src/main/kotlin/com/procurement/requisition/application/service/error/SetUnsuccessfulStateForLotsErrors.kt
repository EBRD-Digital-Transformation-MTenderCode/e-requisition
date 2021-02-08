package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure

sealed class SetUnsuccessfulStateForLotsErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        SetUnsuccessfulStateForLotsErrors(
            code = "VR.COM-17.16.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class UnknownLot(lotIds: Collection<LotId>) :
        SetUnsuccessfulStateForLotsErrors(
            code = "VR.COM-17.16.2",
            description = "Unknown lot(s) '${lotIds.joinToString { it.underlying }}'."
        )
}
