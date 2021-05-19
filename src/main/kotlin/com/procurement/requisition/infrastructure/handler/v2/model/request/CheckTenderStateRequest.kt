package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.application.service.model.command.CheckTenderStateCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class CheckTenderStateRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: String,
    @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: String
)

private val allowedProcurementMethodDetails = ProcurementMethodDetails.allowedElements
    .asSequence()
    .filter {
        when (it) {
            ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
            ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF -> true

            ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
            ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
            ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
            ProcurementMethodDetails.DCO, ProcurementMethodDetails.TEST_DCO,
            ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
            ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
            ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP,
            ProcurementMethodDetails.MC, ProcurementMethodDetails.TEST_MC,
            ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
            ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
            ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP,
            ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
            ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ,
            ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
            ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV -> false
        }
    }
    .toSet()

private val allowedOperationType = OperationType.allowedElements
    .asSequence()
    .filter {
        when (it) {
            OperationType.AWARD_CONSIDERATION,
            OperationType.COMPLETE_SOURCING,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER,
            OperationType.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION,
            OperationType.PCR_PROTOCOL,
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.WITHDRAW_BID,
            OperationType.WITHDRAW_PCR_PROTOCOL -> true

            OperationType.CREATE_PCR,
            OperationType.CREATE_RFQ,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR -> false
        }
    }
    .toSet()

fun CheckTenderStateRequest.convert(): Result<CheckTenderStateCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }

    val pmd = pmd
        .asEnum(target = ProcurementMethodDetails, allowedElements = allowedProcurementMethodDetails)
        .onFailure { return it.repath(path = "/pmd") }

    val operationType = operationType
        .asEnum(target = OperationType, allowedElements = allowedOperationType)
        .onFailure { return it.repath(path = "/operationType") }

    return CheckTenderStateCommand(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType
    ).asSuccess()
}
