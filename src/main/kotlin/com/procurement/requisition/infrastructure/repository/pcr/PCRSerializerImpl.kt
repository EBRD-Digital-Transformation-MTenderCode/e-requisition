package com.procurement.requisition.infrastructure.repository.pcr

import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.infrastructure.repository.pcr.model.mappingToEntity
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class PCRSerializerImpl(val transform: Transform) : PCRSerializer {
    override fun build(pcr: PCR): Result<String, Failure> =
        transform.trySerialization(pcr.mappingToEntity())
            .mapFailure { failure ->
                InternalServerError(
                    description = "Error of serialization 'PCREntity' to JSON.",
                    reason = failure.reason
                )
            }
}
