package com.procurement.requisition.infrastructure.repository.history

import com.datastax.driver.core.Session
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.requisition.infrastructure.extension.cassandra.tryExecute
import com.procurement.requisition.infrastructure.service.HistoryEntity
import com.procurement.requisition.infrastructure.service.HistoryRepository
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraHistoryRepository(private val session: Session) : HistoryRepository {

    companion object {
        private const val KEYSPACE = "requisition"
        private const val HISTORY_TABLE = "history"
        private const val COMMAND_ID = "command_id"
        private const val COMMAND_NAME = "command_name"
        private const val COMMAND_DATE = "command_date"
        const val JSON_DATA = "json_data"

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO $KEYSPACE.$HISTORY_TABLE(
                      $COMMAND_ID,
                      $COMMAND_NAME,
                      $COMMAND_DATE,
                      $JSON_DATA
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT $JSON_DATA
                 FROM $KEYSPACE.$HISTORY_TABLE
                WHERE $COMMAND_ID=? 
                AND $COMMAND_NAME=?
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(commandId: CommandId, commandName: Action): Result<String?, DatabaseIncident> =
        preparedFindHistoryCQL.bind()
            .apply {
                setString(COMMAND_ID, commandId.underlying)
                setString(COMMAND_NAME, commandId.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()?.getString(JSON_DATA)
            .asSuccess()

    override fun saveHistory(entity: HistoryEntity): Result<HistoryEntity, DatabaseIncident> {

        preparedSaveHistoryCQL.bind()
            .apply {
                setString(COMMAND_ID, entity.commandId.underlying)
                setString(COMMAND_NAME, entity.action.key)
                setTimestamp(COMMAND_DATE, entity.date.toCassandraTimestamp())
                setString(JSON_DATA, entity.data)
            }
            .tryExecute(session)
            .onFailure { return it }

        return entity.asSuccess()
    }
}
