package com.procurement.requisition.application.repository.rule

import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface RulesRepository {

    fun get(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType,
        parameter: String
    ): Result<String?, DatabaseIncident>

    fun tenderStates(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType
    ): Result<TenderStatesRule, Failure>
}
