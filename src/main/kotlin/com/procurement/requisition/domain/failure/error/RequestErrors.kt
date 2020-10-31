package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.domain.extension.dot
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandId
import com.procurement.requisition.lib.fail.Failure
import java.util.*

class RequestErrors(
    override val code: String,
    val underlying: String,
    val version: ApiVersion = ApiVersion(0, 0, 0),
    val id: CommandId = UUID.randomUUID().toString(),
    val path: String = "",
    val body: String,
    override val reason: Exception?
) : Failure.Error() {

    override val description: String
        get() = "${underlying.dot()} Request body: ${body}."
}
