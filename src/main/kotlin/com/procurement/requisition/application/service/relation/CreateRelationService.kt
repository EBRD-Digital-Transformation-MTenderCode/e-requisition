package com.procurement.requisition.application.service.relation

import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.relation.model.CreateRelationCommand
import com.procurement.requisition.application.service.relation.model.CreateRelationResult
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
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class CreateRelationService(
    val uriProperties: UriProperties,
    val pcrRepository: PCRRepository,
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

fun relationship(operationType: OperationType): Result<Relationship, Failure> = when (operationType) {
    OperationType.CREATE_PCR -> Relationship.X_PRE_AWARD_CATALOG_REQUEST.asSuccess()
    OperationType.SUBMIT_BID_IN_PCR -> TODO()
    OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR -> TODO()
    OperationType.TENDER_PERIOD_END_IN_PCR -> TODO()
}

fun uri(prefix: String, cpid: Cpid, relatedOcid: Ocid) = "$prefix/${cpid.underlying}/${relatedOcid.underlying}"
