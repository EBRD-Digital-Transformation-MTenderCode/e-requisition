package com.procurement.requisition.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.handler.AbstractHandler
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHandlerV2 : AbstractHandler() {

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    override fun handle(descriptor: CommandDescriptor): Result<String?, Failure> = execute(descriptor)

    fun getParams(node: JsonNode): Result<JsonNode, JsonErrors> = node.tryGetAttribute("params")
}
