package com.procurement.requisition.infrastructure.web.v2

import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.lib.enumerator.EnumElementProvider

object CommandsV2 {

    enum class CommandType(override val key: String, override val kind: Action.Kind) :
        EnumElementProvider.Element, Action {

        CHECK_LOTS_STATE("checkLotsState", kind = Action.Kind.QUERY),
        CHECK_TENDER_STATE("checkTenderState", kind = Action.Kind.QUERY),
        CREATE_PCR(key = "createPcr", kind = Action.Kind.COMMAND),
        CREATE_RELATION_TO_CONTRACT_PROCESS_STAGE("createRelationToContractProcessStage", kind = Action.Kind.COMMAND),
        FIND_ITEMS_BY_LOT_IDS("findItemsByLotIds", kind = Action.Kind.QUERY),
        FIND_PROCUREMENT_METHOD_MODALITIES("findProcurementMethodModalities", kind = Action.Kind.QUERY),
        GET_CURRENCY("getCurrency", kind = Action.Kind.QUERY),
        GET_TENDER_STATE("getTenderState", kind = Action.Kind.QUERY),
        VALIDATE_PCR_DATA("validatePcrData", kind = Action.Kind.QUERY),
        VALIDATE_REQUIREMENT_RESPONSES("validateRequirementResponses", kind = Action.Kind.QUERY),
        ;

        override fun toString(): String = key

        companion object : EnumElementProvider<CommandType>(info = info())
    }
}
