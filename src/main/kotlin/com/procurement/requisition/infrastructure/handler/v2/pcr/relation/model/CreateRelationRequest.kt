package com.procurement.requisition.infrastructure.handler.v2.pcr.relation.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.relation.model.CreateRelationCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class CreateRelationRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("relatedOcid") @param:JsonProperty("relatedOcid") val relatedOcid: String,
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: String
)

val allowedOperationType = OperationType.allowedElements
    .asSequence()
    .filter {
        when (it) {
            OperationType.CREATE_PCR -> true

            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR -> false
        }
    }
    .toSet()

fun CreateRelationRequest.convert(): Result<CreateRelationCommand, JsonErrors> {
    val cpid = Cpid.tryCreateOrNull(cpid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/cpid",
                actualValue = cpid,
                expectedFormat = Cpid.pattern,
                reason = null
            )
        )
    val ocid = Ocid.SingleStage.tryCreateOrNull(ocid)
        ?: Ocid.MultiStage.tryCreateOrNull(ocid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/ocid",
                actualValue = ocid,
                expectedFormat = "${Ocid.SingleStage.pattern}' or '${Ocid.MultiStage.pattern}",
                reason = null
            )
        )
    val relatedOcid = Ocid.SingleStage.tryCreateOrNull(relatedOcid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/relatedOcid",
                actualValue = relatedOcid,
                expectedFormat = Ocid.SingleStage.pattern,
                reason = null
            )
        )
    val operationType =
        operationType.asEnum(target = OperationType, path = "#/operationType", allowedElements = allowedOperationType)
            .onFailure { return it }

    return CreateRelationCommand(
        cpid = cpid,
        ocid = ocid,
        relatedOcid = relatedOcid,
        operationType = operationType
    ).asSuccess()
}

