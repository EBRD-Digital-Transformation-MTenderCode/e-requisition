package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.deserializer.MinSpecificWeightPriceRuleDeserializer
import com.procurement.requisition.application.service.rule.model.MinSpecificWeightPriceRule
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.repository.rule.model.MinSpecificWeightPriceEntity
import com.procurement.requisition.infrastructure.repository.rule.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class MinSpecificWeightPriceRuleDeserializerImpl(val transform: Transform) : MinSpecificWeightPriceRuleDeserializer {
    override fun deserialize(json: String): Result<MinSpecificWeightPriceRule, Failure> =
        json.deserialization<MinSpecificWeightPriceEntity>(transform)
            .converting(MinSpecificWeightPriceEntity::convert)
}
