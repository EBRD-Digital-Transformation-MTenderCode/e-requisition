package com.procurement.requisition.domain.failure.incident

import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.lib.fail.Failure

sealed class RuleIncident : Failure.Incident(level = Level.ERROR, number = "03") {

    override val reason: Exception?
        get() = null

    class NotFound(
        val country: String,
        val pmd: ProcurementMethodDetails,
        val operationType: OperationType,
        val parameter: String
    ) : RuleIncident() {

        override val description: String
            get() = "Rule '$parameter' by country '$country' and pmd '${pmd.key}' and operation type '${operationType.key}' is not found."
    }
}
