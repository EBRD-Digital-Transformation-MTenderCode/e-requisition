package com.procurement.requisition.application.service.set.tender

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class SetTenderStatusSuspendedErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        SetTenderStatusSuspendedErrors(
            code = "400.${GlobalProperties.service.id}.00.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )
}
