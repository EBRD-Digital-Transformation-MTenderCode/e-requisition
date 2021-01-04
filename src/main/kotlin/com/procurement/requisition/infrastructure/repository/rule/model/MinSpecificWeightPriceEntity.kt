package com.procurement.requisition.infrastructure.repository.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.rule.model.MinSpecificWeightPriceRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import java.math.BigDecimal

class MinSpecificWeightPriceEntity(
    @field:JsonProperty("goods") @param:JsonProperty("goods") val goods: BigDecimal,
    @field:JsonProperty("services") @param:JsonProperty("services") val services: BigDecimal,
    @field:JsonProperty("works") @param:JsonProperty("works") val works: BigDecimal
)

fun MinSpecificWeightPriceEntity.convert(): Result<MinSpecificWeightPriceRule, Failure> =
        MinSpecificWeightPriceRule(goods = goods, services = services, works = works).asSuccess()
