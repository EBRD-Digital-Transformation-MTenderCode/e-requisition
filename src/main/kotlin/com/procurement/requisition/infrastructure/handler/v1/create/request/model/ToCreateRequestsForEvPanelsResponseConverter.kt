package com.procurement.requisition.infrastructure.handler.v1.create.request.model

import com.procurement.requisition.application.service.create.request.model.CreatedRequestsForEvPanels
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.serialization

fun CreatedRequestsForEvPanels.convert() = CreateRequestsForEvPanelsResponse(
    criteria = criteria.convert()
)

fun CreatedRequestsForEvPanels.Criterion.convert() = CreateRequestsForEvPanelsResponse.Criterion(
    id = id.underlying,
    title = title,
    description = description,
    source = source.asString(),
    relatesTo = relatesTo.asString(),
    requirementGroups = requirementGroups
        .map { requirementGroup -> requirementGroup.convert() }
)

fun CreatedRequestsForEvPanels.Criterion.RequirementGroup.convert() =
    CreateRequestsForEvPanelsResponse.Criterion.RequirementGroup(
        id = id.underlying,
        requirements = requirements.map { it.serialization() }
    )
