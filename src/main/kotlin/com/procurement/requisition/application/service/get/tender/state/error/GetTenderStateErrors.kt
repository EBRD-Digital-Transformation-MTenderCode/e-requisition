package com.procurement.requisition.application.service.get.tender.state.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.lib.fail.Failure

sealed class GetTenderStateErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class TenderNotFound(cpid: Cpid, ocid: Ocid) : GetTenderStateErrors(
        code = "VR.COM-17.4.1",
        description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
    )
}
