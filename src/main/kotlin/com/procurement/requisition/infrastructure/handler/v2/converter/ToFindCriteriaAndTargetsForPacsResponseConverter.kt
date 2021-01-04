package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.result.FindCriteriaAndTargetsForPacsResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v2.model.response.FindCriteriaAndTargetsForPacsResponse

fun FindCriteriaAndTargetsForPacsResult.convert() = FindCriteriaAndTargetsForPacsResponse(
    tender = tender.convert()
)

fun FindCriteriaAndTargetsForPacsResult.Tender.convert() = FindCriteriaAndTargetsForPacsResponse.Tender(
    targets = targets.map { it.convert() },
    criteria = criteria.map { it.convert() }
)

fun FindCriteriaAndTargetsForPacsResult.Tender.Target.convert() = FindCriteriaAndTargetsForPacsResponse.Tender.Target(
    id = id.underlying,
    observations = observations.map { it.convert() }
)

fun FindCriteriaAndTargetsForPacsResult.Tender.Target.Observation.convert() =
    FindCriteriaAndTargetsForPacsResponse.Tender.Target.Observation(
        id = id.underlying,
        unit = unit.convert(),
        relatedRequirementId = relatedRequirementId,
    )

fun FindCriteriaAndTargetsForPacsResult.Tender.Target.Observation.Unit.convert() =
    FindCriteriaAndTargetsForPacsResponse.Tender.Target.Observation.Unit(id = id, name = name)

fun FindCriteriaAndTargetsForPacsResult.Tender.Criterion.convert() =
    FindCriteriaAndTargetsForPacsResponse.Tender.Criterion(
        id = id.underlying,
        title = title,
        relatesTo = relatesTo?.asString(),
        relatedItem = relatedItem,
        requirementGroups = requirementGroups.map { it.convert() },
    )

fun FindCriteriaAndTargetsForPacsResult.Tender.Criterion.RequirementGroup.convert() =
    FindCriteriaAndTargetsForPacsResponse.Tender.Criterion.RequirementGroup(
        id = id.underlying,
        requirements = requirements.map { it.convert() },
    )

fun FindCriteriaAndTargetsForPacsResult.Tender.Criterion.RequirementGroup.Requirement.convert() =
    FindCriteriaAndTargetsForPacsResponse.Tender.Criterion.RequirementGroup.Requirement(
        id = id,
        title = title
    )
