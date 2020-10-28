package com.procurement.requisition.infrastructure.repository.pcr

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.infrastructure.repository.pcr.model.PCREntity
import com.procurement.requisition.infrastructure.repository.pcr.model.deserialization
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class PCRDeserializerImpl(val transform: Transform) : PCRDeserializer {
    override fun build(json: String): Result<PCR, Failure> =
        transform.tryDeserialization(json, PCREntity::class.java)
            .mapFailure { failure ->
                InternalServerError(
                    description = "Error of deserialization JSON to 'PCREntity'. Json: '$json'.",
                    reason = failure.reason
                )
            }
            .onFailure { return it }
            .deserialization()
            .mapFailure { failure ->
                DatabaseIncident.Data(
                    description = "Error of mapping 'PCREntity' to 'PCR'. ${failure.description}",
                    reason = failure.reason
                )
            }
}
