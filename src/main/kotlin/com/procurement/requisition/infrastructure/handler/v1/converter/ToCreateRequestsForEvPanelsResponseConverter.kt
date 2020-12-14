package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.model.result.CreatedRequestsForEvPanelsResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v1.model.response.CreateRequestsForEvPanelsResponse
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.serialization

fun CreatedRequestsForEvPanelsResult.convert() = CreateRequestsForEvPanelsResponse(
    criteria = criteria.convert()
)

fun CreatedRequestsForEvPanelsResult.Criterion.convert() = CreateRequestsForEvPanelsResponse.Criterion(
    id = id.underlying,
    title = title,
    description = description,
    source = source.asString(),
    relatesTo = relatesTo.asString(),
    requirementGroups = requirementGroups
        .map { requirementGroup -> requirementGroup.convert() }
)

fun CreatedRequestsForEvPanelsResult.Criterion.RequirementGroup.convert() =
    CreateRequestsForEvPanelsResponse.Criterion.RequirementGroup(
        id = id.underlying,
        requirements = requirements.map { it.serialization() }
    )
