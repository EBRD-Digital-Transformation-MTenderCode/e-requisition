package com.procurement.requisition.infrastructure.handler.base

import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.api.version.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface Handler {
    val version: ApiVersion
    val action: Action

    fun handle(descriptor: CommandDescriptor): Result<String?, Failure>
}
