package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.domain.extension.dot
import com.procurement.requisition.infrastructure.web.dto.ApiVersion
import com.procurement.requisition.infrastructure.web.dto.CommandId
import com.procurement.requisition.lib.fail.Failure
import java.util.*

class RequestErrors(
    override val code: String,
    val underlying: String,
    val version: ApiVersion = ApiVersion(0, 0, 0),
    val id: CommandId = UUID.randomUUID().toString(),
    val path: String = "",
    val body: String,
    reason: Exception?
) : Failure.Error(reason = reason) {

    override val description: String
        get() = "${underlying.dot()} Request body: ${body}."
}
