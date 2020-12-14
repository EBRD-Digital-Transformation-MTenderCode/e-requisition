package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.command.ValidateRequirementResponsesCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.lot.RelatedLots
import com.procurement.requisition.infrastructure.handler.converter.asBidId
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asItemId
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asRequirementResponseId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.v2.model.request.ValidateRequirementResponsesRequest
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun ValidateRequirementResponsesRequest.convert(): Result<ValidateRequirementResponsesCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }
    val tender = tender?.convert()?.onFailure { return it.repath("/tender") }
    val bids = bids.convert().onFailure { return it.repath(path = "/bids") }

    return ValidateRequirementResponsesCommand(
        cpid = cpid,
        ocid = ocid,
        tender = tender,
        bids = bids
    ).asSuccess()
}

/**
 * Tender
 */
fun ValidateRequirementResponsesRequest.Tender.convert(): Result<ValidateRequirementResponsesCommand.Tender, JsonErrors> {
    val procurementMethodModalities = procurementMethodModalities
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(target = ProcurementMethodModality)
                .onFailure { return it.repath(path = "/procurementMethodModalities[$idx]") }
        }

    return ValidateRequirementResponsesCommand.Tender(
        procurementMethodModalities = procurementMethodModalities
    ).asSuccess()
}

/**
 * Bids
 */
fun ValidateRequirementResponsesRequest.Bids.convert(): Result<ValidateRequirementResponsesCommand.Bids, JsonErrors> {
    val details = details
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "details")) }
        .mapIndexedOrEmpty { idx, detail ->
            detail.convert().onFailure { return it.repath(path = "/details[$idx]") }
        }
    return ValidateRequirementResponsesCommand.Bids(details = details).asSuccess()
}

/**
 * Detail
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.convert(): Result<ValidateRequirementResponsesCommand.Bids.Detail, JsonErrors> {
    val id = id.asBidId().onFailure { return it.repath(path = "/id") }
    val relatedLots = relatedLots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "relatedLots")) }
        .mapIndexedOrEmpty { idx, relatedLot ->
            relatedLot.asLotId().onFailure { return it.repath("/relatedLots[$idx]") }
        }
        .let { t -> RelatedLots(t) }
    val items = items
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.convert().onFailure { return it.repath(path = "/items[$idx]") }
        }
    val requirementResponses = requirementResponses
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "lots")) }
        .mapIndexedOrEmpty { idx, requirementResponse ->
            requirementResponse.convert().onFailure { return it.repath(path = "/requirementResponses[$idx]") }
        }

    return ValidateRequirementResponsesCommand.Bids.Detail(
        id = id,
        relatedLots = relatedLots,
        items = items,
        requirementResponses = requirementResponses
    ).asSuccess()
}

/**
 * Item
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.Item.convert(): Result<ValidateRequirementResponsesCommand.Bids.Detail.Item, JsonErrors> {
    val id = id.asItemId().onFailure { return it.repath(path = "/id") }
    return ValidateRequirementResponsesCommand.Bids.Detail.Item(
        id = id,
    ).asSuccess()
}

/**
 * RequirementResponse
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.RequirementResponse.convert(): Result<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse, JsonErrors> {
    val id = id.asRequirementResponseId().onFailure { return it.repath(path = "/id") }
    val requirement = requirement.convert().onFailure { return it.repath(path = "/requirement") }
    val period = period?.convert()?.onFailure { return it.repath(path = "/period") }

    return ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse(
        id = id,
        value = value,
        requirement = requirement,
        period = period,
    ).asSuccess()
}

/**
 * Requirement
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.RequirementResponse.Requirement.convert(): Result<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Requirement, JsonErrors> =
    ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Requirement(id = id)
        .asSuccess()

/**
 * Period
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.RequirementResponse.Period.convert():
    Result<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Period, JsonErrors> {

    val startDate = startDate.asLocalDateTime().onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate.asLocalDateTime().onFailure { return it.repath(path = "/endDate") }
    return ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Period(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}
