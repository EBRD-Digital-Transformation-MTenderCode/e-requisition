package com.procurement.requisition.application.repository.rule.deserializer

import com.procurement.requisition.application.service.rule.model.ValidTenderStatesRule
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface TenderStatesRuleDeserializer {
    fun deserialize(json: String): Result<ValidTenderStatesRule, Failure>
}
