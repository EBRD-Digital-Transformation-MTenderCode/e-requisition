package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.GetOcidFromRelatedProcessErrors
import com.procurement.requisition.application.service.model.command.GetOcidFromRelatedProcessCommand
import com.procurement.requisition.application.service.model.result.GetOcidFromRelatedProcessResult
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcess
import com.procurement.requisition.domain.model.relatedprocesses.Relationship
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import org.springframework.stereotype.Service

@Service
class GetOcidFromRelatedProcessService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetOcidFromRelatedProcessCommand): Result<GetOcidFromRelatedProcessResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return GetOcidFromRelatedProcessErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asFailure()

        val appropriateRelationship = getAppropriateRelationship(command.operationType)

        val relatedProcess = pcr.relatedProcesses
            .firstOrNull { relatedProcess -> containsRelationship(relatedProcess, appropriateRelationship) }
            ?: return GetOcidFromRelatedProcessErrors.RelationshipNotFound(appropriateRelationship)
                .asFailure()

        val relatedProcessOcid = Ocid.SingleStage.tryCreateOrNull(relatedProcess.identifier)!!

        return Result.success(GetOcidFromRelatedProcessResult(relatedProcessOcid))
    }

    private fun getAppropriateRelationship(operationType: GetOcidFromRelatedProcessCommand.OperationType): Relationship =
        when (operationType) {
            GetOcidFromRelatedProcessCommand.OperationType.COMPLETE_SOURCING -> Relationship.FRAMEWORK
        }

    private fun containsRelationship(relatedProcess: RelatedProcess, relationship: Relationship) =
        relatedProcess.relationship.any { it == relationship }
}
