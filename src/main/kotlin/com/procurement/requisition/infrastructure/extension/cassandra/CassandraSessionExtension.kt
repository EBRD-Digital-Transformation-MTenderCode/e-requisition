package com.procurement.requisition.infrastructure.extension.cassandra

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.Result.Companion.success

fun BoundStatement.tryExecute(session: Session): Result<ResultSet, DatabaseIncident> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(DatabaseIncident.Access(description = "", reason = expected))
}

fun BatchStatement.tryExecute(session: Session): Result<ResultSet, DatabaseIncident> = try {
    success(session.execute(this))
} catch (expected: Exception) {
    failure(DatabaseIncident.Access(description = "", reason = expected))
}
