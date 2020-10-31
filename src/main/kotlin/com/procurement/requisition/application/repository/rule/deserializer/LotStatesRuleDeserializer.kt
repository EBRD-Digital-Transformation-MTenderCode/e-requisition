package com.procurement.requisition.application.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.model.LotStatesRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface LotStatesRuleDeserializer {
    fun deserialize(json: String): Result<LotStatesRule, Failure>
}
