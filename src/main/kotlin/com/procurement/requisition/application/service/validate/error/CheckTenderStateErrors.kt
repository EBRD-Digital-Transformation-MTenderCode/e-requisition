package com.procurement.requisition.application.service.validate.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.fail.Failure

sealed class CheckTenderStateErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckTenderStateErrors(
            code = "VR.COM-17.6.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class InvalidTenderState(status:TenderStatus, statusDetails: TenderStatusDetails) :
        CheckTenderStateErrors(
            code = "VR.COM-17.6.2",
            description = "Invalid tender state (status '${status.key}', statusDetails ${statusDetails.key})."
        )

}
