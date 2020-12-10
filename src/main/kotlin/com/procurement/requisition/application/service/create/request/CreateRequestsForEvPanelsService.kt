package com.procurement.requisition.application.service.create.request

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.application.service.create.request.model.CreateRequestsForEvPanelsCommand
import com.procurement.requisition.application.service.create.request.model.CreatedRequestsForEvPanels
import com.procurement.requisition.application.service.get.lot.error.GetActiveLotIdsErrors
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
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
) {

    fun create(command: CreateRequestsForEvPanelsCommand): Result<CreatedRequestsForEvPanels, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return GetActiveLotIdsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val tender = pcr.tender
        val criteria = tender.criteria
        val newCriterion = generateCriterion()

        val updatedCriteria = criteria + newCriterion
        val updatedTender = tender.copy(
            criteria = updatedCriteria
        )
        val updatedPCR = pcr.copy(tender = updatedTender)

        val json = pcrSerializer.build(updatedPCR).onFailure { return it }
        val state = TenderState(status = pcr.tender.status, statusDetails = pcr.tender.statusDetails)
        pcrRepository.update(
            cpid = command.cpid,
            ocid = command.ocid,
            state = state,
            data = json
        ).onFailure { return it }

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

    fun Criterion.convert() = CreatedRequestsForEvPanels(
        criteria = CreatedRequestsForEvPanels.Criterion(
            id = id,
            title = title,
            description = description,
            source = source,
            relatesTo = relatesTo!!,
            requirementGroups = requirementGroups
                .map { requirementGroup ->
                    CreatedRequestsForEvPanels.Criterion.RequirementGroup(
                        id = requirementGroup.id,
                        requirements = requirementGroup.requirements.toList()
                    )
                }
        )
    )
}
