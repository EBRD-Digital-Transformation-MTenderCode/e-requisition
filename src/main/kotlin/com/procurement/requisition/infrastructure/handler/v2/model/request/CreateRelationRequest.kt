package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.model.command.CreateRelationCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class CreateRelationRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("relatedOcid") @param:JsonProperty("relatedOcid") val relatedOcid: String,
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: String
)

private val allowedOperationType = OperationType.allowedElements
    .asSequence()
    .filter {
        when (it) {
            OperationType.CREATE_PCR -> true

            OperationType.PCR_PROTOCOL,
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR -> false
        }
    }
    .toSet()

fun CreateRelationRequest.convert(): Result<CreateRelationCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = Ocid.SingleStage.tryCreateOrNull(ocid)
        ?: Ocid.MultiStage.tryCreateOrNull(ocid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                actualValue = ocid,
                expectedFormat = "${Ocid.SingleStage.pattern}' or '${Ocid.MultiStage.pattern}",
            ).repath(path = "/ocid")
        )
    val relatedOcid = relatedOcid.asSingleStageOcid()
        .onFailure { return it.repath(path = "/relatedOcid") }

    val operationType =
        operationType.asEnum(target = OperationType, allowedElements = allowedOperationType)
            .onFailure { return it.repath(path = "/operationType") }

    return CreateRelationCommand(
        cpid = cpid,
        ocid = ocid,
        relatedOcid = relatedOcid,
        operationType = operationType
    ).asSuccess()
}
