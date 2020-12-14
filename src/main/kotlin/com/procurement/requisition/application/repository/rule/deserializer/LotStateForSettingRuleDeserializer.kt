package com.procurement.requisition.application.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.model.LotStateForSettingRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface LotStateForSettingRuleDeserializer {
    fun deserialize(json: String): Result<LotStateForSettingRule, Failure>
}
