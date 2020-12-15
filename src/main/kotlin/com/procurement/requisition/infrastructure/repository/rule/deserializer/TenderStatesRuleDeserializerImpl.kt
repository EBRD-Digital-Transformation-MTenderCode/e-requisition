package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.deserializer.TenderStatesRuleDeserializer
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.rule.model.ValidTenderStatesRule
import com.procurement.requisition.infrastructure.repository.rule.model.ValidTenderStatesRuleEntity
import com.procurement.requisition.infrastructure.repository.rule.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class TenderStatesRuleDeserializerImpl(val transform: Transform) : TenderStatesRuleDeserializer {
    override fun deserialize(json: String): Result<ValidTenderStatesRule, Failure> =
        json.deserialization<ValidTenderStatesRuleEntity>(transform)
            .converting(ValidTenderStatesRuleEntity::convert)
}
