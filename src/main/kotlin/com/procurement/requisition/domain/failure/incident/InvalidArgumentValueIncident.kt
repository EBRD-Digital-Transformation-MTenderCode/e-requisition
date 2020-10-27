package com.procurement.requisition.domain.failure.incident

import com.procurement.requisition.lib.fail.Failure

class InvalidArgumentValueIncident(val name: String, val value: Any, expectedValue: List<Any> = emptyList()) :
    Failure.Incident(level = Level.ERROR, number = "1") {

    override val description: String = "Argument '$name' contains an invalid value '$value'" +
        if (expectedValue.isNotEmpty())
            ", expected value(s) " + expectedValue.joinToString(prefix = "[", postfix = "].")
        else
            "."

    override val reason: Exception = RuntimeException(description)
}
