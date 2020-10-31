package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class ClassificationEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
)

fun Classification.mappingToEntity() = ClassificationEntity(
    id = id,
    scheme = scheme.asString(),
    description = description
)

fun ClassificationEntity.mappingToDomain(): Result<Classification, JsonErrors> {
    val scheme = scheme.asEnum(target = ClassificationScheme)
        .onFailure { return it.repath(path = "/scheme") }
    return Classification(id = id, scheme = scheme, description = description).asSuccess()
}
