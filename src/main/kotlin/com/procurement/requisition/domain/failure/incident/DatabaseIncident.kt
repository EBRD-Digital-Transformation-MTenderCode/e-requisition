package com.procurement.requisition.domain.failure.incident

import com.procurement.requisition.lib.fail.Failure

class DatabaseIncident private constructor(number: String, override val description: String, reason: Exception) :
    Failure.Incident(
        level = Level.ERROR,
        number = "02.$number",
        reason = reason
    ) {

    companion object {
        private const val DESCRIPTION_PREFIX = "Database incident."

        fun access(description: String, cause: Exception) =
            DatabaseIncident(
                number = "01",
                description = "$DESCRIPTION_PREFIX Error of access to database. $description",
                reason = cause
            )

        fun data(description: String, cause: Exception) =
            DatabaseIncident(
                number = "02",
                description = "$DESCRIPTION_PREFIX Invalid data in database. $description",
                reason = cause
            )
    }
}
