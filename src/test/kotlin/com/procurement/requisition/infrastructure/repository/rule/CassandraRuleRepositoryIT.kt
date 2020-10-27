package com.procurement.requisition.infrastructure.repository.rule

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.spy
import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.infrastructure.bind.jackson.configuration
import com.procurement.requisition.infrastructure.repository.CassandraTestContainer
import com.procurement.requisition.infrastructure.repository.DatabaseTestConfiguration
import com.procurement.requisition.infrastructure.service.JacksonJsonTransform
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraRuleRepositoryIT {

    companion object {
        private const val KEYSPACE = "requisition"
        private const val TABLE_NAME = "rules"
        private const val COUNTRY_COLUMN = "country"
        private const val PMD_COLUMN = "pmd"
        private const val OPERATION_TYPE_COLUMN = "operation_type"
        private const val PARAMETER_COLUMN = "parameter"
        private const val VALUE_COLUMN = "value"

        private const val COUNTRY = "MD"
        private val PMD = ProcurementMethodDetails.GPA
        private val OPERATION_TYPE = OperationType.CREATE_PCR
        private const val PARAMETER = "PARAM"
        private const val VALUE = "VAL"
    }

    @Autowired
    private lateinit var container: CassandraTestContainer
    private lateinit var session: Session
    private val transform: Transform = JacksonJsonTransform(ObjectMapper().apply { configuration() })
    private lateinit var repository: RulesRepository

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

        repository = CassandraRulesRepository(session = session, transform = transform)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun get() {
        insertRule()

        val result = repository.get(
            country = COUNTRY,
            pmd = PMD,
            operationType = OPERATION_TYPE,
            parameter = PARAMETER
        )

        assertTrue(result.isSuccess)
        result.forEach {
            assertEquals(it, VALUE)
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
                        $COUNTRY_COLUMN        TEXT,
                        $PMD_COLUMN            TEXT,
                        $OPERATION_TYPE_COLUMN TEXT,
                        $PARAMETER_COLUMN      TEXT,
                        $VALUE_COLUMN          TEXT,
                        PRIMARY KEY ($COUNTRY_COLUMN, $PMD_COLUMN, $OPERATION_TYPE_COLUMN, $PARAMETER_COLUMN)
                    );
            """
        )
    }

    private fun insertRule() {
        val record = QueryBuilder.insertInto(KEYSPACE, TABLE_NAME)
            .value(COUNTRY_COLUMN, COUNTRY)
            .value(PMD_COLUMN, PMD.name)
            .value(OPERATION_TYPE_COLUMN, OPERATION_TYPE.key)
            .value(PARAMETER_COLUMN, PARAMETER)
            .value(VALUE_COLUMN, VALUE)

        session.execute(record)
    }
}
