package com.procurement.requisition.infrastructure.repository.pcr

import com.datastax.driver.core.Session
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.extension.cassandra.tryExecute
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Repository

@Repository
class CassandraPCRRepository(private val session: Session) : PCRRepository {

    companion object {
        private const val KEYSPACE = "requisition"
        private const val TABLE_NAME = "requisitions"
        private const val COLUMN_CPID = "cpid"
        private const val COLUMN_OCID = "ocid"
        private const val COLUMN_TOKEN = "token_entity"
        private const val COLUMN_OWNER = "owner"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_STATUS_DETAIL = "status_details"
        private const val COLUMN_JSON_DATA = "json_data"

        private const val SAVE_NEW_PCR_CQL = """
               INSERT INTO $KEYSPACE.$TABLE_NAME(
                      $COLUMN_CPID,
                      $COLUMN_OCID,
                      $COLUMN_TOKEN,
                      $COLUMN_OWNER,
                      $COLUMN_STATUS,
                      $COLUMN_STATUS_DETAIL,
                      $COLUMN_JSON_DATA
               )
               VALUES(?, ?, ?, ?, ?, ?, ?)
               IF NOT EXISTS
            """
    }

    private val preparedSaveNewPCRCQL = session.prepare(SAVE_NEW_PCR_CQL)

    override fun saveNew(
        cpid: Cpid,
        ocid: Ocid,
        token: Token,
        owner: String,
        status: TenderStatus,
        statusDetails: TenderStatusDetails,
        data: String
    ): Result<Boolean, DatabaseIncident> = preparedSaveNewPCRCQL.bind()
        .apply {
            setString(COLUMN_CPID, cpid.underlying)
            setString(COLUMN_OCID, ocid.underlying)
            setString(COLUMN_TOKEN, token.underlying)
            setString(COLUMN_OWNER, owner)
            setString(COLUMN_STATUS, status.key)
            setString(COLUMN_STATUS_DETAIL, statusDetails.key)
            setString(COLUMN_JSON_DATA, data)
        }
        .tryExecute(session)
        .map { resultSet -> resultSet.wasApplied() }
}
