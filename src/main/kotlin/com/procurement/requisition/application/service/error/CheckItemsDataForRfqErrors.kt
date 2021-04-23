package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure

sealed class CheckItemsDataForRfqErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class LotNotFound(lotId: LotId) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.2",
            description = "Lot '$lotId' is not found."
        )

    class ClassificationMismatch :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.4",
            description = "Classifications in items from request mismatch with classifications into items associated with lot from request."
        )

    class InvalidLotsCount :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.8",
            description = "Expected only one lot in request."
        )

}
