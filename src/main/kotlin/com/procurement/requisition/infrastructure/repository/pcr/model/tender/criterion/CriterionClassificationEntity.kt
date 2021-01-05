package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.tender.criterion.CriterionClassification
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class ClassificationEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
)

fun CriterionClassification.mappingToEntity() = ClassificationEntity(
    id = id,
    scheme = scheme.asString(),
)

fun ClassificationEntity.mappingToDomain(): Result<CriterionClassification, JsonErrors> {
    val scheme = scheme.asEnum(target = ClassificationScheme)
        .onFailure { return it.repath(path = "/scheme") }
    return CriterionClassification(id = id, scheme = scheme).asSuccess()
}
