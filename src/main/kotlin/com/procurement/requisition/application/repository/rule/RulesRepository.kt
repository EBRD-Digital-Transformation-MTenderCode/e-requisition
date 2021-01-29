package com.procurement.requisition.application.repository.rule

import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface RulesRepository {

    fun find(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType? = null,
        parameter: String
    ): Result<String?, DatabaseIncident>

    fun get(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType? = null,
        parameter: String
    ): Result<String, Failure>
}
