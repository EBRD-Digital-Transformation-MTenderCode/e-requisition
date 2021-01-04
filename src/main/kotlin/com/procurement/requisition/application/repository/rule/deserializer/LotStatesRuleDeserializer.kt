package com.procurement.requisition.application.repository.rule.deserializer

import com.procurement.requisition.application.service.rule.model.ValidLotStatesRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface LotStatesRuleDeserializer {
    fun deserialize(json: String): Result<ValidLotStatesRule, Failure>
}
