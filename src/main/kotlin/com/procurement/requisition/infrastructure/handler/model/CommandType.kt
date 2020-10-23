package com.procurement.requisition.infrastructure.handler.model

import com.procurement.requisition.infrastructure.handler.Action
import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class CommandType(override val key: String) : EnumElementProvider.Element, Action {

    CREATE_PCR("createPcr"),
    CREATE_RELATION_TO_CONTRACT_PROCESS_STAGE("createRelationToContractProcessStage"),
    GET_TENDER_STATE("getTenderState"),
    VALIDATE_PCR_DATA("validatePcrData");

    override fun toString(): String = key

    companion object : EnumElementProvider<CommandType>(info = info())
}
