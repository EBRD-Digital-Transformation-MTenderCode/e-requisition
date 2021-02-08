package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.model.tender.criterion.CriterionClassification

data class ClassificationEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
)

fun CriterionClassification.mappingToEntity() = ClassificationEntity(
    id = id,
    scheme = scheme
)

fun ClassificationEntity.mappingToDomain(): CriterionClassification =
    CriterionClassification(id = id, scheme = scheme)

