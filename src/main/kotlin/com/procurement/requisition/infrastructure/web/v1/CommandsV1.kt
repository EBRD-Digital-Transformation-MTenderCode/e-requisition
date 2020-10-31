package com.procurement.requisition.infrastructure.web.v1

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.infrastructure.extension.tryGetAttribute
import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.v1.CommandContext
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.lib.functional.Result

object CommandsV1 {

    val apiVersion: ApiVersion
        get() = ApiVersion(1, 0, 0)

    enum class CommandType(override val key: String, override val kind: Action.Kind) :
        EnumElementProvider.Element, Action {

        CREATE_REQUESTS_FOR_EV_PANELS(key = "createRequestsForEvPanels", kind = Action.Kind.COMMAND),
        GET_ACTIVE_LOTS(key = "getActiveLots", kind = Action.Kind.QUERY),
        GET_AWARD_CRITERIA_AND_CONVERSIONS(key = "getAwardCriteriaAndConversions", kind = Action.Kind.QUERY),
        GET_CURRENCY("getCurrency", kind = Action.Kind.QUERY),
        GET_TENDER_OWNER(key = "getTenderOwner", kind = Action.Kind.QUERY),
        GET_LOTS_AUCTION("getLotsAuction", kind = Action.Kind.QUERY),
        SET_LOTS_STATUS_UNSUCCESSFUL(key = "setLotsStatusUnsuccessful", kind = Action.Kind.COMMAND),
        SET_TENDER_STATUS_DETAILS(key = "setTenderStatusDetails", kind = Action.Kind.COMMAND),
        SET_TENDER_STATUS_UNSUCCESSFUL(key = "setTenderUnsuccessful", kind = Action.Kind.COMMAND),
        ;

        override fun toString(): String = key

        companion object : EnumElementProvider<CommandType>(info = info())
    }

    fun getContext(node: JsonNode): Result<CommandContext, JsonErrors> = node.tryGetAttribute("context")
        .map { CommandContext(it) }

    fun getData(node: JsonNode): Result<JsonNode, JsonErrors> = node.tryGetAttribute("data")
}
