package com.procurement.requisition.application.repository.pcr

import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.functional.Result

interface PCRRepository {

    fun saveNew(
        cpid: Cpid,
        ocid: Ocid,
        token: Token,
        owner: String,
        status: TenderStatus,
        statusDetails: TenderStatusDetails,
        data: String
    ): Result<Boolean, DatabaseIncident>
}
