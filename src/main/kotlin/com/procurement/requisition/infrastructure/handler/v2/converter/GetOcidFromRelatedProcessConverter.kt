package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.application.service.model.command.GetOcidFromRelatedProcessCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.GetOcidFromRelatedProcessRequest
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

private val allowedOperationTypes = OperationType.allowedElements
    .asSequence()
    .filter {
        when (it) {
            OperationType.COMPLETE_SOURCING -> true
            OperationType.CREATE_PCR,
            OperationType.PCR_PROTOCOL,
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR,
            OperationType.WITHDRAW_PCR_PROTOCOL -> false
        }
    }
    .toSet()

fun GetOcidFromRelatedProcessRequest.convert(): Result<GetOcidFromRelatedProcessCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    val operationType = operationType.asEnum(target = OperationType, allowedElements = allowedOperationTypes)
        .onFailure { return it.repath(path = "/operationType") }

    return GetOcidFromRelatedProcessCommand(cpid = cpid, ocid = ocid, operationType = operationType)
        .asSuccess()
}
