package com.procurement.requisition.infrastructure.repository.pcr

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.nhaarman.mockito_kotlin.spy
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.domain.extension.nowDefaultUTC
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Stage
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.infrastructure.repository.CassandraTestContainer
import com.procurement.requisition.infrastructure.repository.DatabaseTestConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraPCRRepositoryIT {

    companion object {
        private const val KEYSPACE = "requisition"
        private const val TABLE_NAME = "requisitions"
        private const val CPID_COLUMN = "cpid"
        private const val OCID_COLUMN = "ocid"
        private const val TOKEN_ENTITY_COLUMN = "token_entity"
        private const val OWNER_COLUMN = "owner"
        private const val STATUS_COLUMN = "status"
        private const val STATUS_DETAILS_COLUMN = "status_details"
        private const val JSON_DATA_COLUMN = "json_data"

        private val CPID = Cpid.generate(prefix = "ocds", country = "MD", timestamp = nowDefaultUTC())
        private val OCID = Ocid.SingleStage.generate(cpid = CPID, stage = Stage.PC, timestamp = nowDefaultUTC())
        private val TOKEN = Token.generate()
        private const val OWNER = "owner"
        private val STATUS = TenderStatus.ACTIVE
        private val STATUS_UPDATED = TenderStatus.PLANNED
        private val STATUS_DETAILS = TenderStatusDetails.TENDERING
        private val STATUS_DETAILS_UPDATED = TenderStatusDetails.CLARIFICATION
        private const val JSON_DATA: String = """{"tender": {"title" : "Tender-Title"}}"""
        private const val JSON_UPDATED_DATA: String = """{"tender": {"title" : "Tender-Title-Updated"}}"""
    }

    @Autowired
    private lateinit var container: CassandraTestContainer
    private lateinit var session: Session
    private lateinit var repository: PCRRepository

    @BeforeEach
    fun init() {
        val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        session = spy(cluster.connect())

        createKeyspace()
        createTable()

        repository = CassandraPCRRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun getCredential() {
        repository.saveNew(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            data = JSON_DATA
        )

        val result = repository.getCredential(cpid = CPID, ocid = OCID)
        assertTrue(result.isSuccess)
        result.forEach { credential ->
            assertNotNull(credential)
            assertEquals(TOKEN, credential!!.token)
            assertEquals(OWNER, credential.owner)
        }
    }

    @Test
    fun getPCR() {
        repository.saveNew(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            data = JSON_DATA
        )

        val result = repository.getPCR(cpid = CPID, ocid = OCID)
        assertTrue(result.isSuccess)
        result.forEach { data ->
            assertNotNull(data)
            assertEquals(JSON_DATA, data)
        }
    }

    @Test
    fun getTenderState() {
        repository.saveNew(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            data = JSON_DATA
        )

        val result = repository.getTenderState(cpid = CPID, ocid = OCID)
        assertTrue(result.isSuccess)
        result.forEach { state ->
            assertNotNull(state)
            assertEquals(STATUS, state!!.status)
            assertEquals(STATUS_DETAILS, state.statusDetails)
        }
    }

    @Test
    fun saveNew() {

        val result = repository.saveNew(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            data = JSON_DATA
        )

        assertTrue(result.isSuccess)
        result.forEach {
            assertTrue(it)
        }
    }

    @Test
    fun updateNonExistsPCR() {

        val result = repository.update(
            cpid = CPID,
            ocid = OCID,
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            data = JSON_DATA
        )

        assertTrue(result.isSuccess)
        result.forEach {
            assertFalse(it)
        }
    }

    @Test
    fun update() {

        val resultInsert = repository.saveNew(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = STATUS,
            statusDetails = STATUS_DETAILS,
            data = JSON_DATA
        )

        assertTrue(resultInsert.isSuccess)
        resultInsert.forEach {
            assertTrue(it)
        }

        val resultUpdate = repository.update(
            cpid = CPID,
            ocid = OCID,
            status = STATUS_UPDATED,
            statusDetails = STATUS_DETAILS_UPDATED,
            data = JSON_UPDATED_DATA
        )

        assertTrue(resultUpdate.isSuccess)
        resultUpdate.forEach {
            assertTrue(it)
        }

        val pcr = repository.getPCR(cpid = CPID, ocid = OCID)
        assertTrue(pcr.isSuccess)
        pcr.forEach { data ->
            assertNotNull(data)
            assertEquals(JSON_UPDATED_DATA, data)
        }

        val tenderStateResult = repository.getTenderState(cpid = CPID, ocid = OCID)
        assertTrue(tenderStateResult.isSuccess)
        tenderStateResult.forEach { state ->
            assertNotNull(state)
            assertEquals(STATUS_UPDATED, state!!.status)
            assertEquals(STATUS_DETAILS_UPDATED, state.statusDetails)
        }
    }

    private fun createKeyspace() {
        session.execute(
            "CREATE KEYSPACE $KEYSPACE " +
                "WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};"
        )
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE $KEYSPACE;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS $KEYSPACE.$TABLE_NAME
                    (
                        $CPID_COLUMN           TEXT,
                        $OCID_COLUMN           TEXT,
                        $TOKEN_ENTITY_COLUMN   TEXT,
                        $OWNER_COLUMN          TEXT,
                        $STATUS_COLUMN         TEXT,
                        $STATUS_DETAILS_COLUMN TEXT,
                        $JSON_DATA_COLUMN      TEXT,
                        PRIMARY KEY ($CPID_COLUMN, $OCID_COLUMN)
                    );
            """
        )
    }
}
