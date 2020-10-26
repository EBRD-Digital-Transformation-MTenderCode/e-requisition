package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

interface Handler {
    fun handle(descriptor: CommandDescriptor): Result<String?, Failure>
}
