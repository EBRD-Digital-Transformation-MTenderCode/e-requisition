package com.procurement.requisition.application.service.get.lot.auction

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class GetLotsAuctionErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        GetLotsAuctionErrors(
            code = "400.${GlobalProperties.service.id}.00.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class NoActiveLotsFound(cpid: Cpid, ocid: Ocid) :
        GetLotsAuctionErrors(
            code = "400.${GlobalProperties.service.id}.10.03",
            description = "Active lots in PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' not found."
        )
}
