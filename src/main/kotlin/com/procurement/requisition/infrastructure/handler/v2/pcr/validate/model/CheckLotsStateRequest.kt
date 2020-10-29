package com.procurement.requisition.infrastructure.handler.v2.pcr.validate.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.application.service.validate.model.CheckLotsStateCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.OperationType
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.infrastructure.handler.converter.asEnum
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
            OperationType.SUBMIT_BID_IN_PCR -> true

            OperationType.CREATE_PCR,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR -> false
        }
    }
    .toSet()

fun CheckLotsStateRequest.convert(): Result<CheckLotsStateCommand, JsonErrors> {
    val cpid = Cpid.tryCreateOrNull(cpid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/params/cpid",
                actualValue = cpid,
                expectedFormat = Cpid.pattern,
                reason = null
            )
        )
    val ocid = Ocid.SingleStage.tryCreateOrNull(ocid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/params/ocid",
                actualValue = ocid,
                expectedFormat = Ocid.SingleStage.pattern,
                reason = null
            )
        )

    val pmd = pmd
        .asEnum(
            target = ProcurementMethodDetails,
            path = "#/params/pmd",
            allowedElements = allowedProcurementMethodDetails
        )
        .onFailure { return it }

    val operationType = operationType
        .asEnum(
            target = OperationType,
            path = "#/params/operationType",
            allowedElements = allowedOperationType
        )
        .onFailure { return it }

    val tender = tender.convert(path = "#/params/tender").onFailure { return it }
    return CheckLotsStateCommand(
        cpid = cpid,
        ocid = ocid,
        pmd = pmd,
        country = country,
        operationType = operationType,
        tender = tender
    ).asSuccess()
}

fun CheckLotsStateRequest.Tender.convert(path: String): Result<CheckLotsStateCommand.Tender, JsonErrors> {
    val lots = lots.failureIfEmpty { return Result.failure(JsonErrors.EmptyArray(path = "$path/lots")) }
        .mapIndexed { idx, lot ->
            lot.convert(path = "$path/lots[$idx]").onFailure { return it }
        }

    return CheckLotsStateCommand.Tender(
        lots = lots
    ).asSuccess()
}

fun CheckLotsStateRequest.Tender.Lot.convert(path: String): Result<CheckLotsStateCommand.Tender.Lot, JsonErrors> {
    val id = LotId.orNull(id)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = LotId.pattern,
                reason = null
            )
        )

    return CheckLotsStateCommand.Tender.Lot(id = id).asSuccess()
}
