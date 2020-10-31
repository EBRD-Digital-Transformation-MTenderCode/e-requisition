package com.procurement.requisition.application.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface TenderStatesRuleDeserializer {
    fun deserialize(json: String): Result<TenderStatesRule, Failure>
}
