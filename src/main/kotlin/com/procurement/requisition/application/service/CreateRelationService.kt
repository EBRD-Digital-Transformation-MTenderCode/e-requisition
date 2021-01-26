package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.model.command.CreateRelationCommand
import com.procurement.requisition.application.service.model.result.CreateRelationResult
import com.procurement.requisition.domain.failure.incident.InvalidArgumentValueIncident
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcess
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessScheme
import com.procurement.requisition.domain.model.relatedprocesses.Relationship
import com.procurement.requisition.domain.model.relatedprocesses.Relationships
import com.procurement.requisition.infrastructure.configuration.properties.UriProperties
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class CreateRelationService(
    val uriProperties: UriProperties,
) {

    fun create(command: CreateRelationCommand): Result<CreateRelationResult, Failure> {
        val relationship = relationship(command.operationType)
            .onFailure { return it }

        val relatedProcess = RelatedProcess(
            id = RelatedProcessId.generate(),
            scheme = RelatedProcessScheme.OCID,
            identifier = command.relatedOcid.underlying,
            relationship = Relationships(relationship),
            uri = uri(prefix = uriProperties.tender, cpid = command.cpid, relatedOcid = command.relatedOcid)
        )

        return CreateRelationResult(
            relatedProcesses = listOf(
                CreateRelationResult.RelatedProcess(
                    id = relatedProcess.id,
                    scheme = relatedProcess.scheme,
                    identifier = relatedProcess.identifier,
                    relationship = relatedProcess.relationship,
                    uri = relatedProcess.uri
                )
            )
        ).asSuccess()
    }
}

fun relationship(operationType: OperationType): Result<Relationship, InvalidArgumentValueIncident> =
    when (operationType) {
        OperationType.CREATE_PCR -> Relationship.X_PRE_AWARD_CATALOG_REQUEST.asSuccess()

        OperationType.COMPLETE_SOURCING,
        OperationType.PCR_PROTOCOL,
        OperationType.SUBMIT_BID_IN_PCR,
        OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
        OperationType.TENDER_PERIOD_END_IN_PCR,
        OperationType.WITHDRAW_PCR_PROTOCOL -> InvalidArgumentValueIncident(
            name = "operationType",
            value = operationType,
            expectedValue = listOf(Relationship.X_PRE_AWARD_CATALOG_REQUEST)
        ).asFailure()
    }

fun uri(prefix: String, cpid: Cpid, relatedOcid: Ocid) = "$prefix/${cpid.underlying}/${relatedOcid.underlying}"
