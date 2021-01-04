package com.procurement.requisition.infrastructure.repository.pcr

import com.datastax.driver.core.Session
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.model.Credential
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.extension.cassandra.tryExecute
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
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

        private const val GET_TENDER_STATE_CQL = """
            SELECT ${COLUMN_STATUS}, ${COLUMN_STATUS_DETAIL}
              FROM ${KEYSPACE}.${TABLE_NAME}
             WHERE ${COLUMN_CPID}=?
               AND ${COLUMN_OCID}=?
        """

        private const val GET_CREDENTIAL_CQL = """
            SELECT $COLUMN_TOKEN, $COLUMN_OWNER
              FROM ${KEYSPACE}.${TABLE_NAME}
             WHERE ${COLUMN_CPID}=?
               AND ${COLUMN_OCID}=?
        """

        private const val GET_PCR_CQL = """
            SELECT ${COLUMN_JSON_DATA}
              FROM ${KEYSPACE}.${TABLE_NAME}
             WHERE ${COLUMN_CPID}=?
               AND ${COLUMN_OCID}=?
        """

        private const val UPDATE_PCR_CQL = """
               UPDATE $KEYSPACE.$TABLE_NAME
                  SET $COLUMN_STATUS=?,
                      $COLUMN_STATUS_DETAIL=?,
                      $COLUMN_JSON_DATA=?
                WHERE $COLUMN_CPID=?
                  AND $COLUMN_OCID=?
               IF EXISTS
            """
    }

    private val preparedGetCredentialCQL = session.prepare(GET_CREDENTIAL_CQL)
    private val preparedGetPCRCQL = session.prepare(GET_PCR_CQL)
    private val preparedGetTenderStateCQL = session.prepare(GET_TENDER_STATE_CQL)
    private val preparedSaveNewPCRCQL = session.prepare(SAVE_NEW_PCR_CQL)
    private val preparedUpdatePCRCQL = session.prepare(UPDATE_PCR_CQL)

    override fun getCredential(cpid: Cpid, ocid: Ocid): Result<Credential?, DatabaseIncident> =
        preparedGetCredentialCQL.bind()
            .apply {
                setString(COLUMN_CPID, cpid.underlying)
                setString(COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { row ->
                val token = row.getString(COLUMN_TOKEN)
                    .let {
                        Token.orNull(it)
                            ?: return Result.failure(DatabaseIncident.Data(description = "Error of parsing token. Unknown value '$it'."))
                    }
                val owner = row.getString(COLUMN_OWNER)
                Credential(token = token, owner = owner)
            }
            .asSuccess()

    override fun getPCR(cpid: Cpid, ocid: Ocid): Result<String?, DatabaseIncident> =
        preparedGetPCRCQL.bind()
            .apply {
                setString(COLUMN_CPID, cpid.underlying)
                setString(COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(COLUMN_JSON_DATA)
            .asSuccess()

    override fun getTenderState(cpid: Cpid, ocid: Ocid): Result<TenderState?, DatabaseIncident> =
        preparedGetTenderStateCQL.bind()
            .apply {
                setString(COLUMN_CPID, cpid.underlying)
                setString(COLUMN_OCID, ocid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { row ->
                val status = row.getString(COLUMN_STATUS)
                    .let {
                        TenderStatus.orNull(it)
                            ?: return Result.failure(DatabaseIncident.Data(description = "Error of parsing action. Unknown value '$it'."))
                    }

                val statusDetails = row.getString(COLUMN_STATUS_DETAIL)
                    .let {
                        TenderStatusDetails.orNull(it)
                            ?: return Result.failure(DatabaseIncident.Data(description = "Error of parsing action. Unknown value '$it'."))
                    }

                TenderState(status = status, statusDetails = statusDetails)
            }
            .asSuccess()

    override fun saveNew(
        cpid: Cpid,
        ocid: Ocid,
        credential: Credential,
        state: TenderState,
        data: String
    ): Result<Boolean, DatabaseIncident> = preparedSaveNewPCRCQL.bind()
        .apply {
            setString(COLUMN_CPID, cpid.underlying)
            setString(COLUMN_OCID, ocid.underlying)
            setString(COLUMN_TOKEN, credential.token.underlying)
            setString(COLUMN_OWNER, credential.owner)
            setString(COLUMN_STATUS, state.status.key)
            setString(COLUMN_STATUS_DETAIL, state.statusDetails.key)
            setString(COLUMN_JSON_DATA, data)
        }
        .tryExecute(session)
        .map { resultSet -> resultSet.wasApplied() }

    override fun update(
        cpid: Cpid,
        ocid: Ocid,
        state: TenderState,
        data: String
    ): Result<Boolean, DatabaseIncident> = preparedUpdatePCRCQL.bind()
        .apply {
            setString(COLUMN_CPID, cpid.underlying)
            setString(COLUMN_OCID, ocid.underlying)
            setString(COLUMN_STATUS, state.status.key)
            setString(COLUMN_STATUS_DETAIL, state.statusDetails.key)
            setString(COLUMN_JSON_DATA, data)
        }
        .tryExecute(session)
        .map { resultSet -> resultSet.wasApplied() }
}
