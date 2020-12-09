package com.procurement.requisition.infrastructure.handler.v1.model

import com.procurement.requisition.infrastructure.configuration.GlobalProperties
import com.procurement.requisition.lib.fail.Failure

sealed class CommandMessageErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class MissingAttribute(description: String) : CommandMessageErrors(
        code = "400.${GlobalProperties.service.id}.20.01",
        description = "Context parameter not found. $description"
    )

    class InvalidFormatToken(description: String) : CommandMessageErrors(
        code = "400.${GlobalProperties.service.id}.10.63",
        description = "Invalid format the token. $description"
    )
}
