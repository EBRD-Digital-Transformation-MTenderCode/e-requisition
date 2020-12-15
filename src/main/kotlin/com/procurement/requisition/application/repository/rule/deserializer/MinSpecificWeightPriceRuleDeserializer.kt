package com.procurement.requisition.application.repository.rule.deserializer

import com.procurement.requisition.application.service.rule.model.MinSpecificWeightPriceRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface MinSpecificWeightPriceRuleDeserializer {
    fun deserialize(json: String): Result<MinSpecificWeightPriceRule, Failure>
}
