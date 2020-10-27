package com.procurement.requisition.infrastructure.repository.rule

import com.datastax.driver.core.Session
import com.procurement.requisition.application.repository.rule.RulesRepository
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.failure.incident.RuleIncident
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.infrastructure.extension.cassandra.tryExecute
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Repository

@Repository
class CassandraRulesRepository(
    private val session: Session
) : RulesRepository {

    companion object {

        private const val KEYSPACE = "requisition"
        private const val RULES_TABLE = "rules"
        private const val COLUMN_COUNTRY = "country"
        private const val COLUMN_PMD = "pmd"
        private const val COLUMN_OPERATION_TYPE = "operation_type"
        private const val COLUMN_PARAMETER = "parameter"
        private const val COLUMN_VALUE = "value"

        private const val GET_RULE_CQL = """
            SELECT $COLUMN_VALUE
              FROM $KEYSPACE.$RULES_TABLE
             WHERE $COLUMN_COUNTRY=?
               AND $COLUMN_PMD=?
               AND $COLUMN_OPERATION_TYPE=?
               AND $COLUMN_PARAMETER=?
        """
    }

    private val preparedGetRuleCQL = session.prepare(GET_RULE_CQL)

    override fun find(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType,
        parameter: String
    ): Result<String?, DatabaseIncident> = preparedGetRuleCQL.bind()
        .apply {
            this.setString(COLUMN_COUNTRY, country)
            this.setString(COLUMN_PMD, pmd.name)
            this.setString(COLUMN_OPERATION_TYPE, operationType.key)
            this.setString(COLUMN_PARAMETER, parameter)
        }
        .tryExecute(session)
        .onFailure { return it }
        .one()
        ?.getString(COLUMN_VALUE)
        .asSuccess()

    override fun get(
        country: String,
        pmd: ProcurementMethodDetails,
        operationType: OperationType,
        parameter: String
    ): Result<String, Failure> =
        find(country = country, pmd = pmd, operationType = operationType, parameter = parameter)
            .onFailure { return it }
            ?.asSuccess()
            ?: RuleIncident.NotFound(country = country, pmd = pmd, operationType = operationType, parameter = parameter)
                .asFailure()
}
