package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.deserializer.TenderStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.model.TenderStatesRule
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.repository.rule.model.TenderStatesEntity
import com.procurement.requisition.infrastructure.repository.rule.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class TenderStatesRuleDeserializerImpl(val transform: Transform) : TenderStatesRuleDeserializer {
    override fun deserialize(json: String): Result<TenderStatesRule, Failure> =
        json.deserialization<TenderStatesEntity>(transform)
            .converting(TenderStatesEntity::convert)
}
