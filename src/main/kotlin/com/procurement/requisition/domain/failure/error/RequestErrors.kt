package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.lib.fail.Failure

class RequestErrors(
    override val code: String,
    override val description: String,
    val path: String = "",
    val body: String,
    override val reason: Exception?
) : Failure.Error()
