package com.procurement.requisition.infrastructure.handler.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHandlerV1 : AbstractHandler() {

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    override fun handle(descriptor: CommandDescriptor): Result<String?, Failure> = execute(descriptor)

    fun getContext(node: JsonNode): Result<CommandContext, JsonErrors> = node.tryGetAttribute("context")
        .map { CommandContext(it) }

    fun getData(node: JsonNode): Result<JsonNode, JsonErrors> = node.tryGetAttribute("data")
}
