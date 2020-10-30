package com.procurement.requisition.infrastructure.handler.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.infrastructure.extension.tryGetTextAttribute
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import java.time.LocalDateTime

class CommandContext(private val node: JsonNode) {

    val cpid: Result<Cpid, JsonErrors>
        get() = node.tryGetTextAttribute("cpid")
            .flatMap { value ->
                Cpid.tryCreateOrNull(value)
                    ?.asSuccess()
                    ?: Result.failure(
                        JsonErrors.DataFormatMismatch(
                            path = "#/context/cpid",
                            actualValue = value,
                            expectedFormat = Cpid.pattern,
                            reason = null
                        )
                    )
            }

    val ocid: Result<Ocid, JsonErrors>
        get() = node.tryGetTextAttribute("ocid")
            .flatMap { value ->
                Ocid.SingleStage.tryCreateOrNull(value)
                    ?.asSuccess()
                    ?: Result.failure(
                        JsonErrors.DataFormatMismatch(
                            path = "#/context/ocid",
                            actualValue = value,
                            expectedFormat = Ocid.SingleStage.pattern,
                            reason = null
                        )
                    )
            }

    val owner: Result<String, JsonErrors>
        get() = node.tryGetTextAttribute("owner")

    val startDate: Result<LocalDateTime, JsonErrors>
        get() = node.tryGetTextAttribute("startDate")
            .flatMap { value -> value.asLocalDateTime(path = "#/context/startDate") }

    val phase: Result<String, JsonErrors>
        get() = node.tryGetTextAttribute("phase")

    val pmd: Result<String, JsonErrors>
        get() = node.tryGetTextAttribute("pmd")
}
