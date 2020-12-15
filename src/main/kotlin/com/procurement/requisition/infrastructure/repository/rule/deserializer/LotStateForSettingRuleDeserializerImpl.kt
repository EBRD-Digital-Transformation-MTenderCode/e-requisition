package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.deserializer.LotStateForSettingRuleDeserializer
import com.procurement.requisition.application.service.rule.model.LotStateForSettingRule
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.repository.rule.model.LotStateForSettingRuleEntity
import com.procurement.requisition.infrastructure.repository.rule.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class LotStateForSettingRuleDeserializerImpl(val transform: Transform) : LotStateForSettingRuleDeserializer {
    override fun deserialize(json: String): Result<LotStateForSettingRule, Failure> =
        json.deserialization<LotStateForSettingRuleEntity>(transform)
            .converting(LotStateForSettingRuleEntity::convert)
}
