package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class GetItemsByLotIdsErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        GetItemsByLotIdsErrors(
            code = "400.${GlobalProperties.service.id}.17.01",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class NoItemsFoundForLots(lots: Set<LotId>) :
        GetItemsByLotIdsErrors(
            code = "400.${GlobalProperties.service.id}.17.02",
            description = "No items found for lot(s) '${lots.joinToString()}'."
        )
}
