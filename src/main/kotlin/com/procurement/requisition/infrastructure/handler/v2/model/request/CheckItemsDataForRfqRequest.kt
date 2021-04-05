package com.procurement.requisition.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.requisition.application.service.model.command.CheckItemsDataForRfqCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.infrastructure.bind.quantity.QuantityDeserializer
import com.procurement.requisition.infrastructure.bind.quantity.QuantitySerializer
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asItemId
import com.procurement.requisition.infrastructure.handler.converter.asLotId
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import java.math.BigDecimal

data class CheckItemsDataForRfqRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {

    data class Tender(
        @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>,
        @field:JsonProperty("items") @param:JsonProperty("items") val items: List<Item>
    ) {

        data class Lot(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
        )

        data class Item(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("classification") @param:JsonProperty("classification") val classification: Classification,

            @JsonDeserialize(using = QuantityDeserializer::class)
            @JsonSerialize(using = QuantitySerializer::class)
            @field:JsonProperty("quantity") @param:JsonProperty("quantity") val quantity: BigDecimal,

            @field:JsonProperty("unit") @param:JsonProperty("unit") val unit: Unit,
            @field:JsonProperty("relatedLot") @param:JsonProperty("relatedLot") val relatedLot: String
        ) {
            data class Unit(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String
            )

            data class Classification(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String
            )
        }
    }
}

fun CheckItemsDataForRfqRequest.convert(): Result<CheckItemsDataForRfqCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val ocid = ocid.asSingleStageOcid().onFailure { return it.repath(path = "/ocid") }

    val tender = tender.convert().onFailure { return it.repath(path = "/tender") }
    return CheckItemsDataForRfqCommand(
        cpid = cpid,
        ocid = ocid,
        tender = tender
    ).asSuccess()
}

fun CheckItemsDataForRfqRequest.Tender.convert(): Result<CheckItemsDataForRfqCommand.Tender, JsonErrors> {
    val lots = lots.failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "/lots")) }
        .mapIndexed { idx, lot ->
            lot.convert().onFailure { return it.repath(path = "/lots[$idx]") }
        }

    val items = items.failureIfEmpty { return Result.failure(JsonErrors.EmptyArray().repath(path = "/items")) }
        .mapIndexed { idx, item ->
            item.convert().onFailure { return it.repath(path = "/items[$idx]") }
        }

    return CheckItemsDataForRfqCommand.Tender(lots = lots, items = items).asSuccess()
}

fun CheckItemsDataForRfqRequest.Tender.Lot.convert(): Result<CheckItemsDataForRfqCommand.Tender.Lot, JsonErrors> {
    val id = id.asLotId().onFailure { return it.repath(path = "/id") }
    return CheckItemsDataForRfqCommand.Tender.Lot(id = id).asSuccess()
}

fun CheckItemsDataForRfqRequest.Tender.Item.convert(): Result<CheckItemsDataForRfqCommand.Tender.Item, JsonErrors> {
    val id = id.asItemId().onFailure { return it.repath(path = "/id") }
    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }
    val unit = unit.convert()
    val relatedLot = relatedLot.asLotId().onFailure { return it.repath(path = "/relatedLot") }

    return CheckItemsDataForRfqCommand.Tender.Item(
        id = id,
        quantity = quantity,
        classification = classification,
        unit = unit,
        relatedLot = relatedLot
    ).asSuccess()
}

fun CheckItemsDataForRfqRequest.Tender.Item.Classification.convert(): Result<CheckItemsDataForRfqCommand.Tender.Item.Classification, JsonErrors> {
    val scheme = scheme.asEnum(target = ClassificationScheme)
        .onFailure { return it.repath(path = "/scheme") }

    return CheckItemsDataForRfqCommand.Tender.Item.Classification(
        id = id,
        scheme = scheme
    ).asSuccess()
}

fun CheckItemsDataForRfqRequest.Tender.Item.Unit.convert(): CheckItemsDataForRfqCommand.Tender.Item.Unit =
    CheckItemsDataForRfqCommand.Tender.Item.Unit(id = id)
