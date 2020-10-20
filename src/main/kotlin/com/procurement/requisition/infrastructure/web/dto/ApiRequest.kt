package com.procurement.requisition.infrastructure.web.dto

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.infrastructure.handler.Action

data class ApiRequest(
    val body: String,
    val version: ApiVersion,
    val id: CommandId,
    val action: Action,
    val node: JsonNode
)
