package com.procurement.requisition.infrastructure.handler.v2.converter.extension

import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError

fun String?.checkForBlank(path: String): Validated<JsonErrors.EmptyString> =
    if (this != null && this.isBlank())
        JsonErrors.EmptyString().repath(path).asValidatedError()
    else
        Validated.ok()