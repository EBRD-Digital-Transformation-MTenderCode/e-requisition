package com.procurement.requisition.application.service.relation

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.service.relation.error.CreateRelationErrors
import com.procurement.requisition.application.service.relation.model.CreateRelationCommand
import com.procurement.requisition.application.service.relation.model.CreatedRelation
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
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class CreateRelationService(
    val uriProperties: UriProperties,
    val pcrRepository: PCRRepository,
    val pcrSerializer: PCRSerializer,
    val pcrDeserializer: PCRDeserializer,
) {

    fun create(command: CreateRelationCommand): Result<CreatedRelation, Failure> {

        // VR.COM-1.33.1
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json).onFailure { return it } }
            ?: return failure(CreateRelationErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid))

        // VR.COM-1.33.2
        val relationPresent = pcr.relatedProcesses.any { it.identifier == command.ocid.underlying }
        if (relationPresent)
            return failure(CreateRelationErrors.RelationPresent(ocid = command.ocid))

        val relationship = relationship(command.operationType)
            .onFailure { return it }

        val newRelatedProcess = RelatedProcess(
            id = RelatedProcessId.generate(),
            scheme = RelatedProcessScheme.OCID,
            identifier = command.relatedOcid.underlying,
            relationship = Relationships(relationship),
            uri = uri(prefix = uriProperties.tender, cpid = command.cpid, relatedOcid = command.relatedOcid)
        )

        val newRelatedProcesses = pcr.relatedProcesses + newRelatedProcess
        val updatedPCR = pcr.copy(relatedProcesses = newRelatedProcesses)
        val json = pcrSerializer.build(updatedPCR).onFailure { return it }

        pcrRepository.saveNew(
            cpid = command.cpid,
            ocid = command.ocid,
            token = pcr.token,
            owner = pcr.owner,
            status = pcr.tender.status,
            statusDetails = pcr.tender.statusDetails,
            data = json
        ).onFailure { return it }

        return CreatedRelation(
            relatedProcesses = listOf(
                CreatedRelation.RelatedProcess(
                    id = newRelatedProcess.id,
                    scheme = newRelatedProcess.scheme,
                    identifier = newRelatedProcess.identifier,
                    relationship = newRelatedProcess.relationship,
                    uri = newRelatedProcess.uri
                )
            )
        ).asSuccess()
    }
}

fun relationship(operationType: OperationType): Result<Relationship, Failure> = when (operationType) {
    OperationType.CREATE_PCR -> Relationship.X_PRE_AWARD_CATALOG_REQUEST.asSuccess()
    OperationType.TENDER_PERIOD_END_IN_PCR -> TODO()
    OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR -> TODO()
}

fun uri(prefix: String, cpid: Cpid, relatedOcid: Ocid) = "$prefix/tenders/${cpid.underlying}/${relatedOcid.underlying}"
