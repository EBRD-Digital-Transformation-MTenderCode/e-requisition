package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.CreateRequestsForEvPanelsErrors
import com.procurement.requisition.application.service.model.command.CreateRequestsForEvPanelsCommand
import com.procurement.requisition.application.service.model.result.CreatedRequestsForEvPanelsResult
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroup
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.RequirementGroups
import com.procurement.requisition.domain.model.requirement.Requirements
import com.procurement.requisition.domain.model.requirement.generateRequirementId
import com.procurement.requisition.domain.model.tender.criterion.Criterion
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class CreateRequestsForEvPanelsService(
    private val pcrManagement: PCRManagementService,
) {

    fun create(command: CreateRequestsForEvPanelsCommand): Result<CreatedRequestsForEvPanelsResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return CreateRequestsForEvPanelsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val tender = pcr.tender
        val criteria = tender.criteria
        val newCriterion = generateCriterion()

        val updatedCriteria = criteria + newCriterion
        val updatedTender = tender.copy(
            criteria = updatedCriteria
        )
        val updatedPCR = pcr.copy(tender = updatedTender)

        pcrManagement.update(cpid = command.cpid, ocid = command.ocid, pcr = updatedPCR)
            .onFailure { return it }

        return newCriterion.convert().asSuccess()
    }

    fun generateCriterion(): Criterion = Criterion(
        id = CriterionId.generate(),
        title = "",
        description = "",
        source = CriterionSource.PROCURING_ENTITY,
        relatesTo = CriterionRelatesTo.AWARD,
        relatedItem = null,
        requirementGroups = RequirementGroups(
            RequirementGroup(
                id = RequirementGroupId.generate(),
                description = null,
                requirements = Requirements(
                    Requirement(
                        id = generateRequirementId(),
                        title = "",
                        dataType = DynamicValue.DataType.BOOLEAN,
                        period = null,
                        description = null
                    )
                )
            )
        )
    )

    fun Criterion.convert() = CreatedRequestsForEvPanelsResult(
        criteria = CreatedRequestsForEvPanelsResult.Criterion(
            id = id,
            title = title,
            description = description,
            source = source,
            relatesTo = relatesTo!!,
            requirementGroups = requirementGroups
                .map { requirementGroup ->
                    CreatedRequestsForEvPanelsResult.Criterion.RequirementGroup(
                        id = requirementGroup.id,
                        requirements = requirementGroup.requirements.toList()
                    )
                }
        )
    )
}
