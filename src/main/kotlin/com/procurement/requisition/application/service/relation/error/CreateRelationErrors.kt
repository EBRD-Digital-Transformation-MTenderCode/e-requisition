package com.procurement.requisition.application.service.relation.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.lib.fail.Failure

sealed class CreateRelationErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) : CreateRelationErrors(code = "VR.COM-1.33.1", description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found.")

    class RelationPresent(ocid: Ocid) : CreateRelationErrors(code = "VR.COM-1.33.1", description = "Related process with identifier '${ocid.underlying}' is already.")
}
