package com.procurement.requisition.infrastructure.repository.rule.error

import com.procurement.requisition.lib.fail.Failure

sealed class RuleIncident : Failure.Incident(level = Level.ERROR, number = "03") {

    override val reason: Exception?
        get() = null

    class NotFound(override val description: String) : RuleIncident()
}
