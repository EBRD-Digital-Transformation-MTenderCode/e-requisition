package com.procurement.requisition.infrastructure.repository.pcr.model.tender.lot

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.lot.Variant
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class VariantEntity(
    @field:JsonProperty("hasVariants") @param:JsonProperty("hasVariants") val hasVariants: Boolean,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("variantsDetails") @param:JsonProperty("variantsDetails") val variantsDetails: String?
)

fun Variant.serialization() =
    VariantEntity(hasVariants = hasVariants, variantsDetails = variantsDetails)

fun VariantEntity.deserialization(path: String): Result<Variant, JsonErrors> =
    Variant(hasVariants = hasVariants, variantsDetails = variantsDetails).asSuccess()
