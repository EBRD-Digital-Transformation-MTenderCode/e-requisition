package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.lib.fail.Failure

sealed class FindCriteriaAndTargetsForPacsErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        FindCriteriaAndTargetsForPacsErrors(
            code = "VR.COM-17.13.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )
}
