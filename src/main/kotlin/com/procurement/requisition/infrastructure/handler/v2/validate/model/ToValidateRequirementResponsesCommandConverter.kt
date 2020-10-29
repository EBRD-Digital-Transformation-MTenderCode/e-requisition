package com.procurement.requisition.infrastructure.handler.v2.validate.model

import com.procurement.requisition.application.service.validate.model.ValidateRequirementResponsesCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.RelatedLots
import com.procurement.requisition.infrastructure.handler.converter.asBidId
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asItemId
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asRequirementResponseId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun ValidateRequirementResponsesRequest.convert(): Result<ValidateRequirementResponsesCommand, JsonErrors> {
    val cpid = cpid.asCpid(path = "#/params/cpid").onFailure { return it }
    val ocid = ocid.asSingleStageOcid(path = "#/params/ocid").onFailure { return it }
    val tender = tender?.convert("#/params/tender")?.onFailure { return it }
    val bids = bids.convert("#/params/bids").onFailure { return it }

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
fun ValidateRequirementResponsesRequest.Tender.convert(path: String): Result<ValidateRequirementResponsesCommand.Tender, JsonErrors> {
    val procurementMethodModalities = procurementMethodModalities
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(
                target = ProcurementMethodModality,
                path = "$path/procurementMethodModalities[$idx]"
            )
                .onFailure { return it }
        }

    return ValidateRequirementResponsesCommand.Tender(
        procurementMethodModalities = procurementMethodModalities
    ).asSuccess()
}

/**
 * Bids
 */
fun ValidateRequirementResponsesRequest.Bids.convert(path: String): Result<ValidateRequirementResponsesCommand.Bids, JsonErrors> {
    val details = details
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/details")) }
        .mapIndexedOrEmpty { idx, detail ->
            detail.convert(path = "$path/details[$idx]")
                .onFailure { return it }
        }
    return ValidateRequirementResponsesCommand.Bids(details = details).asSuccess()
}

/**
 * Detail
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.convert(path: String): Result<ValidateRequirementResponsesCommand.Bids.Detail, JsonErrors> {
    val id = id.asBidId(path = "$path/id").onFailure { return it }
    val relatedLots = relatedLots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/relatedLots")) }
        .mapIndexedOrEmpty { idx, relatedLot ->
            LotId.orNull(relatedLot)
                ?: return failure(
                    JsonErrors.DataFormatMismatch(
                        path = "$path/relatedLots[$idx]",
                        actualValue = relatedLot,
                        expectedFormat = LotId.pattern
                    )
                )
        }
        .let { t -> RelatedLots(t) }
    val items = items
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.convert(path = "$path/items[$idx]").onFailure { return it }
        }
    val requirementResponses = requirementResponses
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/lots")) }
        .mapIndexedOrEmpty { idx, requirementResponse ->
            requirementResponse.convert(path = "$path/requirementResponses[$idx]")
                .onFailure { return it }
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
fun ValidateRequirementResponsesRequest.Bids.Detail.Item.convert(path: String): Result<ValidateRequirementResponsesCommand.Bids.Detail.Item, JsonErrors> {
    val id = id.asItemId(path = "$path/id").onFailure { return it }
    return ValidateRequirementResponsesCommand.Bids.Detail.Item(
        id = id,
    ).asSuccess()
}

/**
 * RequirementResponse
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.RequirementResponse.convert(path: String): Result<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse, JsonErrors> {
    val id = id.asRequirementResponseId(path = "$path/id").onFailure { return it }
    val requirement = requirement.convert(path = "$path/requirement").onFailure { return it }
    val period = period?.convert(path = "$path/period")?.onFailure { return it }

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
fun ValidateRequirementResponsesRequest.Bids.Detail.RequirementResponse.Requirement.convert(path: String): Result<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Requirement, JsonErrors> =
    ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Requirement(id = id)
        .asSuccess()

/**
 * Period
 */
fun ValidateRequirementResponsesRequest.Bids.Detail.RequirementResponse.Period.convert(path: String):
    Result<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Period, JsonErrors> {

    val startDate = endDate.asLocalDateTime(path = "$path/startDate")
        .onFailure { return it }

    val endDate = endDate.asLocalDateTime(path = "$path/endDate")
        .onFailure { return it }

    return ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Period(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}
