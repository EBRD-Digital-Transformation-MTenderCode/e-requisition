package com.procurement.requisition.domain.failure.incident

import com.procurement.requisition.lib.fail.Failure

sealed class DatabaseIncident(
    number: String,
    override val description: String
) : Failure.Incident(level = Level.ERROR, number = "02.$number") {

    companion object {
        private const val DESCRIPTION_PREFIX = "Database incident."
    }

    class Access(description: String, override val reason: Exception) :
        DatabaseIncident(number = "01", description = "$DESCRIPTION_PREFIX Error of access to database. $description")

    class Data(description: String, override val reason: Exception? = null) :
        DatabaseIncident(number = "02", description = "$DESCRIPTION_PREFIX Invalid data in database. $description")
}
