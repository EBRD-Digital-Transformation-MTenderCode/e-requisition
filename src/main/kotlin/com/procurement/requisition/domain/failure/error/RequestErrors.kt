package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.domain.extension.dot
import com.procurement.requisition.lib.fail.Failure

class RequestErrors(
    override val code: String,
    description: String,
    val path: String = "",
    val body: String,
    override val reason: Exception?
) : Failure.Error() {

    override val description: String = "${description.dot()} Request body: ${body}."
}
