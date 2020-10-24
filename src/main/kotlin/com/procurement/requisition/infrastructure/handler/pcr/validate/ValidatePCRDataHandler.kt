package com.procurement.requisition.infrastructure.handler.pcr.validate

import com.procurement.requisition.application.extension.tryMapping
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.validate.ValidatePCRService
import com.procurement.requisition.domain.failure.error.RequestErrors
import com.procurement.requisition.infrastructure.handler.AbstractQueryHandler
import com.procurement.requisition.infrastructure.handler.getParams
import com.procurement.requisition.infrastructure.handler.model.ApiRequest
import com.procurement.requisition.infrastructure.handler.model.ApiResponse
import com.procurement.requisition.infrastructure.handler.pcr.validate.model.ValidatePCRDataRequest
import com.procurement.requisition.infrastructure.handler.pcr.validate.model.convert
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess

class ValidatePCRDataHandler(
    override val logger: Logger,
    override val transform: Transform,
    val validatePCRService: ValidatePCRService
) : AbstractQueryHandler() {

    override fun execute(request: ApiRequest): Result<ApiResponse, Failure> {

        val params = request.node.getParams()
            .onFailure { failure -> return failure }
            .tryMapping<ValidatePCRDataRequest>(transform)
            .mapFailure { failure ->
                RequestErrors(
                    code = "RQ-1",
                    version = request.version,
                    id = request.id,
                    body = request.body,
                    underlying = failure.description,
                    path = "params",
                    reason = failure.reason
                )
            }
            .onFailure { failure -> return failure }
            .convert()
            .onFailure { failure -> return failure }

        return when (val result = validatePCRService.validate(params)) {
            is Validated.Success ->
                ApiResponse.Success(version = request.version, id = request.id, result = null)
                    .asSuccess()

            is Validated.Error -> result.reason.asFailure()
        }
    }
}
