package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.lib.fail.Failure

sealed class CheckAccessToTenderErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckAccessToTenderErrors(
            code = "VR.COM-17.11.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    object InvalidToken : CheckAccessToTenderErrors(code = "VR.COM-17.11.2", description = "Invalid token.")

    object InvalidOwner : CheckAccessToTenderErrors(code = "VR.COM-17.11.3", description = "Invalid owner.")
}
