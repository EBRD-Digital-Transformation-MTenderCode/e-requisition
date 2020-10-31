package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Amount
import com.procurement.requisition.domain.model.tender.Value
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import java.math.BigDecimal

data class ValueEntity(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal?,

    @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
)

fun Value.mappingToEntity() = ValueEntity(amount = amount?.underlying, currency = currency)

fun ValueEntity.mappingToDomain(): Result<Value, JsonErrors> =
    Value(amount = amount?.let { Amount(it) }, currency = currency).asSuccess() //TODO
