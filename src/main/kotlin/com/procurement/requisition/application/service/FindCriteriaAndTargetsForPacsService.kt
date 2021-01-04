package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.error.FindCriteriaAndTargetsForPacsErrors
import com.procurement.requisition.application.service.model.command.FindCriteriaAndTargetsForPacsCommand
import com.procurement.requisition.application.service.model.result.FindCriteriaAndTargetsForPacsResult
import com.procurement.requisition.domain.model.tender.criterion.Criteria
import com.procurement.requisition.domain.model.tender.criterion.Criterion
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service

@Service
class FindCriteriaAndTargetsForPacsService(
    private val pcrManagement: PCRManagementService
) {

    fun find(command: FindCriteriaAndTargetsForPacsCommand): Result<FindCriteriaAndTargetsForPacsResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?: return FindCriteriaAndTargetsForPacsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asFailure()

        val lotIds = command.tender.lots.toSet { it.id }
        val itemIds = pcr.tender.items
            .asSequence()
            .filter { item -> item.relatedLot in lotIds }
            .map { item -> item.id }
            .toSet()

        val selectedCriteria = pcr.tender.criteria.select(lotIds, itemIds)

        return FindCriteriaAndTargetsForPacsResult(
            tender = FindCriteriaAndTargetsForPacsResult.Tender(
                targets = pcr.tender.targets
                    .map { target ->
                        FindCriteriaAndTargetsForPacsResult.Tender.Target(
                            id = target.id,
                            observations = target.observations
                                .map { observation ->
                                    FindCriteriaAndTargetsForPacsResult.Tender.Target.Observation(
                                        id = observation.id,
                                        unit = observation.unit
                                            .let { unit ->
                                                FindCriteriaAndTargetsForPacsResult.Tender.Target.Observation.Unit(
                                                    id = unit.id,
                                                    name = unit.name
                                                )
                                            },
                                        relatedRequirementId = observation.relatedRequirementId
                                    )
                                }
                        )
                    },
                criteria = selectedCriteria
                    .map { criterion ->
                        FindCriteriaAndTargetsForPacsResult.Tender.Criterion(
                            id = criterion.id,
                            title = criterion.title,
                            relatesTo = criterion.relatesTo,
                            relatedItem = criterion.relatedItem,
                            requirementGroups = criterion.requirementGroups
                                .map { requirementGroup ->
                                    FindCriteriaAndTargetsForPacsResult.Tender.Criterion.RequirementGroup(
                                        id = requirementGroup.id,
                                        requirements = requirementGroup.requirements
                                            .map { requirement ->
                                                FindCriteriaAndTargetsForPacsResult.Tender.Criterion.RequirementGroup.Requirement(
                                                    id = requirement.id,
                                                    title = requirement.title
                                                )
                                            }
                                    )
                                }
                        )
                    }

            )
        ).asSuccess()
    }

    fun Criteria.select(lotIds: Collection<LotId>, itemIds: Collection<ItemId>): List<Criterion> {
        val lotRelatedItems = lotIds.toSet { it.underlying }
        val itemRelatedItems = itemIds.toSet { it.underlying }
        return this.filter { criterion ->
            criterion.ofTender() ||
                criterion.ofLot(lotRelatedItems) ||
                criterion.ofItem(itemRelatedItems)
        }
    }

    fun Criterion.ofTender() = this.relatesTo == null || this.relatesTo == CriterionRelatesTo.TENDER

    fun Criterion.ofLot(ids: Set<String>) = this.relatesTo == CriterionRelatesTo.LOT && this.relatedItem in ids

    fun Criterion.ofItem(ids: Set<String>) = this.relatesTo == CriterionRelatesTo.ITEM && this.relatedItem in ids
}
