package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

abstract class AbstractHandler : Handler {

    abstract val transform: Transform
    abstract val logger: Logger

    override fun handle(descriptor: CommandDescriptor): Result<String?, Failure> = execute(descriptor)

    abstract fun execute(descriptor: CommandDescriptor): Result<String?, Failure>
}
