package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.repository.rule.deserializer.LotStatesRuleDeserializer
import com.procurement.requisition.application.repository.rule.model.LotStatesRule
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.repository.rule.model.LotStatusesEntity
import com.procurement.requisition.infrastructure.repository.rule.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class LotStatesRuleDeserializerImpl(val transform: Transform) : LotStatesRuleDeserializer {
    override fun deserialize(json: String): Result<LotStatesRule, Failure> =
        json.deserialization<LotStatusesEntity>(transform)
            .converting(LotStatusesEntity::convert)
}
