package com.procurement.requisition.infrastructure.repository.pcr

import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.TransformErrors
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.infrastructure.repository.pcr.model.serialization
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class PCRSerializerImpl(val transform: Transform) : PCRSerializer {
    override fun build(pcr: PCR): Result<String, TransformErrors.Serialization> =
        transform.trySerialization(pcr.serialization())
}
