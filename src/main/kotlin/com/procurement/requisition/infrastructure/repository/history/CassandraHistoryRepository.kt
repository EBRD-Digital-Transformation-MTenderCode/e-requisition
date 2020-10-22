package com.procurement.requisition.infrastructure.repository.history

import com.datastax.driver.core.Session
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.requisition.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.requisition.infrastructure.extension.cassandra.tryExecute
import com.procurement.requisition.infrastructure.handler.model.CommandId
import com.procurement.requisition.infrastructure.handler.model.CommandType
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
               SELECT $COMMAND_ID,
                      $COMMAND_NAME,
                      $COMMAND_DATE,
                      $JSON_DATA
                 FROM $KEYSPACE.$HISTORY_TABLE
                WHERE $COMMAND_ID=?
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(commandId: CommandId): Result<HistoryEntity?, DatabaseIncident> =
        preparedFindHistoryCQL.bind()
            .apply {
                setString(COMMAND_ID, commandId)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { row ->
                val action = row.getString(COMMAND_NAME)
                    .let {
                        CommandType.orNull(it)
                            ?: return Result.failure(DatabaseIncident.Data(description = "Error of parsing action. Unknown value '$it'."))
                    }
                HistoryEntity(
                    commandId = row.getString(COMMAND_ID),
                    action = action,
                    date = row.getTimestamp(COMMAND_DATE).toLocalDateTime(),
                    data = row.getString(JSON_DATA)
                )
            }
            .asSuccess()

    override fun saveHistory(entity: HistoryEntity): Result<HistoryEntity, DatabaseIncident> {

        preparedSaveHistoryCQL.bind()
            .apply {
                setString(COMMAND_ID, entity.commandId)
                setString(COMMAND_NAME, entity.action.key)
                setTimestamp(COMMAND_DATE, entity.date.toCassandraTimestamp())
                setString(JSON_DATA, entity.data)
            }
            .tryExecute(session)
            .onFailure { return it }

        return entity.asSuccess()
    }
}
