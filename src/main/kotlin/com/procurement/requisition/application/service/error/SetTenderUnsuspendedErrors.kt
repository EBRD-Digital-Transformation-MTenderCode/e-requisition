package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class SetTenderUnsuspendedErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        SetTenderUnsuspendedErrors(
            code = "400.${GlobalProperties.service.id}.00.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class TenderStatusDetailsParseFailed(phase: String) :
        SetTenderUnsuspendedErrors(
            code = "400.${GlobalProperties.service.id}.00.00",
            description = "Unknown value for enumType ${TenderStatusDetails::class.java.canonicalName}: $phase, Allowed values are ${TenderStatusDetails.allowedElements}"
        )

    class TenderNotUnsuspendedError :
        SetTenderUnsuspendedErrors(
            code = "400.${GlobalProperties.service.id}.10.27",
            description = "Invalid tender status details. Please retry saving the answer."
        )
}
