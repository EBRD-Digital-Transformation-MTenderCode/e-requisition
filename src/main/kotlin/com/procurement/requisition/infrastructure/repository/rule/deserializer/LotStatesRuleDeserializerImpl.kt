package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.deserializer.LotStatesRuleDeserializer
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.rule.model.ValidLotStatesRule
import com.procurement.requisition.infrastructure.repository.rule.model.ValidLotStatesRuleEntity
import com.procurement.requisition.infrastructure.repository.rule.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class LotStatesRuleDeserializerImpl(val transform: Transform) : LotStatesRuleDeserializer {
    override fun deserialize(json: String): Result<ValidLotStatesRule, Failure> =
        json.deserialization<ValidLotStatesRuleEntity>(transform)
            .converting(ValidLotStatesRuleEntity::convert)
}
