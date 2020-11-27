package com.procurement.requisition.infrastructure.service

import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.lib.functional.Result
import java.time.LocalDateTime

interface HistoryRepository {
    fun getHistory(commandId: CommandId, commandName: Action): Result<String?, DatabaseIncident>
    fun saveHistory(entity: HistoryEntity): Result<HistoryEntity, DatabaseIncident>
}

data class HistoryEntity(
    var commandId: CommandId,
    var action: Action,
    var date: LocalDateTime,
    var data: String
)
