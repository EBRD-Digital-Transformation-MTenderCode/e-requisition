package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.GetCriteriaForTendererErrors
import com.procurement.requisition.application.service.model.command.GetCriteriaForTendererCommand
import com.procurement.requisition.application.service.model.result.GetCriteriaForTendererResult
import com.procurement.requisition.domain.model.requirement.RequirementStatus
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetCriteriaForTendererService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetCriteriaForTendererCommand): Result<GetCriteriaForTendererResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return GetCriteriaForTendererErrors.PCRNotFound(command.cpid, command.ocid).asFailure()

        val criteriaForTenderer = pcr.tender.criteria
            .filter { it.source == CriterionSource.TENDERER }
            .map { criterion -> GetCriteriaForTendererResult.fromDomain(criterion) }

        val criteriaWithActiveRequirements = criteriaForTenderer
            .map {
                it.copy(
                    requirementGroups = it.requirementGroups
                        .map {
                            it.copy(requirements = it.requirements.filter { it.status == RequirementStatus.ACTIVE })
                        }
                        .filter { it.requirements.isNotEmpty() }
                )
            }
            .filter { it.requirementGroups.isNotEmpty() }

        return GetCriteriaForTendererResult(criteriaWithActiveRequirements).asSuccess()

    }
}
