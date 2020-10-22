package com.procurement.requisition.domain.failure.incident

import com.procurement.requisition.lib.fail.Failure

class InternalServerError(override val description: String, override val reason: Exception) :
    Failure.Incident(level = Level.ERROR, number = "0")
