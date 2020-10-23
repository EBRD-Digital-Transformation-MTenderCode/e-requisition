package com.procurement.requisition.infrastructure.repository.pcr.model.tender.target.observation

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.target.observation.Dimensions
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class DimensionsEntity(
    @param:JsonProperty("requirementClassIdPR") @field:JsonProperty("requirementClassIdPR") val requirementClassIdPR: String
)

fun Dimensions.serialization() = DimensionsEntity(requirementClassIdPR = requirementClassIdPR)

fun DimensionsEntity.deserialization(path: String): Result<Dimensions, JsonErrors> =
    Dimensions(requirementClassIdPR = requirementClassIdPR).asSuccess()
