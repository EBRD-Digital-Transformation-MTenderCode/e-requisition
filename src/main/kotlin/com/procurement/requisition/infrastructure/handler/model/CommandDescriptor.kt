package com.procurement.requisition.infrastructure.handler.model

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.infrastructure.api.command.id.CommandId
import com.procurement.requisition.infrastructure.api.Action

data class CommandDescriptor(
    val version: ApiVersion,
    val id: CommandId,
    val action: Action,
    val body: Body
) {
    data class Body(val asString: String, val asJsonNode: JsonNode)

    companion object
}
