package com.procurement.requisition.infrastructure.handler.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.infrastructure.extension.tryGetTextAttribute
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.functional.Result
import java.time.LocalDateTime

class CommandContext(private val node: JsonNode) {

    val cpid: Result<Cpid, JsonErrors>
        get() = node.tryGetTextAttribute("/cpid")
            .flatMap { value -> value.asCpid() }
            .repath(path = "/context")

    val ocid: Result<Ocid, JsonErrors>
        get() = node.tryGetTextAttribute("/ocid")
            .flatMap { value -> value.asSingleStageOcid() }
            .repath(path = "/context")

    val owner: Result<String, JsonErrors>
        get() = node.tryGetTextAttribute("/owner")
            .repath(path = "/context")

    val startDate: Result<LocalDateTime, JsonErrors>
        get() = node.tryGetTextAttribute("/startDate")
            .flatMap { value -> value.asLocalDateTime() }
            .repath(path = "/context")

    val phase: Result<String, JsonErrors>
        get() = node.tryGetTextAttribute("/phase")
            .repath(path = "/context")

    val pmd: Result<String, JsonErrors>
        get() = node.tryGetTextAttribute("pmd")
            .repath(path = "/context")
}
