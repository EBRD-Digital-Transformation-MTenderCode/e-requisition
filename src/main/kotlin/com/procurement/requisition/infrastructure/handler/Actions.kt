package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class Actions(override val key: String, override val kind: Action.Kind) : EnumElementProvider.Element, Action {

    CHECK_ACCESS_TO_TENDER(key = "checkAccessToTender", kind = Action.Kind.QUERY),
    CHECK_LOTS_STATE(key = "checkLotsState", kind = Action.Kind.QUERY),
    CHECK_LOTS_STATUS(key = "checkLotsStatus", kind = Action.Kind.QUERY),
    CHECK_LOT_AWARDED(key = "checkLotAwarded", kind = Action.Kind.QUERY),
    CHECK_TENDER_STATE(key = "checkTenderState", kind = Action.Kind.QUERY),
    CREATE_PCR(key = "createPcr", kind = Action.Kind.COMMAND),
    CREATE_RELATION_TO_CONTRACT_PROCESS_STAGE(key = "createRelationToContractProcessStage", kind = Action.Kind.COMMAND),
    CREATE_REQUESTS_FOR_EV_PANELS(key = "createRequestsForEvPanels", kind = Action.Kind.COMMAND),
    FIND_CRITERIA_AND_TARGETS_FOR_PACS(key = "findCriteriaAndTargetsForPacs", kind = Action.Kind.QUERY),
    FIND_ITEMS_BY_LOT_IDS(key = "findItemsByLotIds", kind = Action.Kind.QUERY),
    FIND_PROCUREMENT_METHOD_MODALITIES(key = "findProcurementMethodModalities", kind = Action.Kind.QUERY),
    GET_ACTIVE_LOTS(key = "getActiveLots", kind = Action.Kind.QUERY),
    GET_AWARD_CRITERIA_AND_CONVERSIONS(key = "getAwardCriteriaAndConversions", kind = Action.Kind.QUERY),
    GET_CRITERIA_FOR_TENDERER(key = "getCriteriaForTenderer", kind = Action.Kind.QUERY),
    GET_CURRENCY(key = "getCurrency", kind = Action.Kind.QUERY),
    GET_LOTS_AUCTION(key = "getLotsAuction", kind = Action.Kind.QUERY),
    GET_TENDER_OWNER(key = "getTenderOwner", kind = Action.Kind.QUERY),
    GET_TENDER_STATE(key = "getTenderState", kind = Action.Kind.QUERY),
    SET_LOTS_STATE(key = "setStateForLots", kind = Action.Kind.COMMAND),
    SET_LOTS_STATUS_UNSUCCESSFUL(key = "setLotsStatusUnsuccessful", kind = Action.Kind.COMMAND),
    SET_TENDER_STATUS_DETAILS(key = "setTenderStatusDetails", kind = Action.Kind.COMMAND),
    SET_TENDER_STATUS_UNSUCCESSFUL(key = "setTenderUnsuccessful", kind = Action.Kind.COMMAND),
    SET_TENDER_SUSPENDED(key = "setTenderSuspended", kind = Action.Kind.COMMAND),
    SET_TENDER_UNSUSPENDED(key = "setTenderUnsuspended", kind = Action.Kind.COMMAND),
    SET_UNSUCCESSFUL_STATE_FOR_LOTS(key = "setUnsuccessfulStateForLots", kind = Action.Kind.COMMAND),
    VALIDATE_PCR_DATA(key = "validatePcrData", kind = Action.Kind.QUERY),
    VALIDATE_REQUIREMENT_RESPONSES(key = "validateRequirementResponses", kind = Action.Kind.QUERY),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<Actions>(info = info())
}
