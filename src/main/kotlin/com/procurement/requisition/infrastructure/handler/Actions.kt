package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class Actions(override val key: String, override val kind: Action.Kind) : EnumElementProvider.Element, Action {

    CHECK_LOTS_STATE("checkLotsState", kind = Action.Kind.QUERY),
    CHECK_TENDER_STATE("checkTenderState", kind = Action.Kind.QUERY),
    CREATE_PCR(key = "createPcr", kind = Action.Kind.COMMAND),
    CREATE_RELATION_TO_CONTRACT_PROCESS_STAGE("createRelationToContractProcessStage", kind = Action.Kind.COMMAND),
    CREATE_REQUESTS_FOR_EV_PANELS(key = "createRequestsForEvPanels", kind = Action.Kind.COMMAND),
    FIND_ITEMS_BY_LOT_IDS("findItemsByLotIds", kind = Action.Kind.QUERY),
    FIND_PROCUREMENT_METHOD_MODALITIES("findProcurementMethodModalities", kind = Action.Kind.QUERY),
    GET_ACTIVE_LOTS(key = "getActiveLots", kind = Action.Kind.QUERY),
    GET_AWARD_CRITERIA_AND_CONVERSIONS(key = "getAwardCriteriaAndConversions", kind = Action.Kind.QUERY),
    GET_CURRENCY("getCurrency", kind = Action.Kind.QUERY),
    GET_LOTS_AUCTION("getLotsAuction", kind = Action.Kind.QUERY),
    GET_TENDER_OWNER(key = "getTenderOwner", kind = Action.Kind.QUERY),
    GET_TENDER_STATE("getTenderState", kind = Action.Kind.QUERY),
    SET_LOTS_STATUS_UNSUCCESSFUL(key = "setLotsStatusUnsuccessful", kind = Action.Kind.COMMAND),
    SET_TENDER_STATUS_DETAILS(key = "setTenderStatusDetails", kind = Action.Kind.COMMAND),
    SET_TENDER_STATUS_UNSUCCESSFUL(key = "setTenderUnsuccessful", kind = Action.Kind.COMMAND),
    VALIDATE_PCR_DATA("validatePcrData", kind = Action.Kind.QUERY),
    VALIDATE_REQUIREMENT_RESPONSES("validateRequirementResponses", kind = Action.Kind.QUERY),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<Actions>(info = info())
}
