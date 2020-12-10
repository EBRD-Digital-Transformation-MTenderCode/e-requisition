package com.procurement.requisition.application.repository.pcr

import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.lib.functional.Result

interface PCRRepository {

    fun getCredential(cpid: Cpid, ocid: Ocid): Result<Credential?, DatabaseIncident>

    fun getPCR(cpid: Cpid, ocid: Ocid): Result<String?, DatabaseIncident>

    fun getTenderState(cpid: Cpid, ocid: Ocid): Result<TenderState?, DatabaseIncident>

    fun saveNew(
        cpid: Cpid,
        ocid: Ocid,
        credential: Credential,
        status: TenderStatus,
        statusDetails: TenderStatusDetails,
        data: String
    ): Result<Boolean, DatabaseIncident>

    fun update(
        cpid: Cpid,
        ocid: Ocid,
        status: TenderStatus,
        statusDetails: TenderStatusDetails,
        data: String
    ): Result<Boolean, DatabaseIncident>
}

data class Credential(val token: Token, val owner: String)
