package com.procurement.requisition.application.service.set.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class SetTenderStatusDetailsErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        SetTenderStatusDetailsErrors(
            code = "400.${GlobalProperties.service.id}.00.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class CalculateTenderStatusDetails(phase: String) :
        SetTenderStatusDetailsErrors(
            code = "400.${GlobalProperties.service.id}.01.01",
            description = "Error of calculate tender status details by phase '$phase'."
        )
}
