package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.application.service.model.command.CheckLotsStateCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class CheckLotsStateRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("pmd") @param:JsonProperty("pmd") val pmd: String,
    @field:JsonProperty("country") @param:JsonProperty("country") val country: String,
    @field:JsonProperty("operationType") @param:JsonProperty("operationType") val operationType: String,
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(
        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
        )
    }
}

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
            OperationType.PCR_PROTOCOL,
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.WITHDRAW_PCR_PROTOCOL -> true

            OperationType.CREATE_PCR,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR,
            OperationType.WITHDRAW_BID -> false
        }
    }
    .toSet()

fun CheckLotsStateRequest.convert(): Result<CheckLotsStateCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }

    val pmd = pmd
        .asEnum(target = ProcurementMethodDetails, allowedElements = allowedProcurementMethodDetails)
        .onFailure { return it.repath(path = "/pmd") }

    val operationType = operationType
        .asEnum(target = OperationType, allowedElements = allowedOperationType)
        .onFailure { return it.repath(path = "/operationType") }

    val tender = tender.convert().onFailure { return it.repath(path = "/tender") }
    return CheckLotsStateCommand(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType,
        tender = tender
    ).asSuccess()
}

fun CheckLotsStateRequest.Tender.convert(): Result<CheckLotsStateCommand.Tender, JsonErrors> {
    val lots = lots.failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "/lots")) }
        .mapIndexed { idx, lot ->
            lot.convert().onFailure { return it.repath(path = "/lots[$idx]") }
        }

    return CheckLotsStateCommand.Tender(
        lots = lots
    ).asSuccess()
}

fun CheckLotsStateRequest.Tender.Lot.convert(): Result<CheckLotsStateCommand.Tender.Lot, JsonErrors> {
    val id = id.asLotId().onFailure { return it.repath(path = "/id") }
    return CheckLotsStateCommand.Tender.Lot(id = id).asSuccess()
}
