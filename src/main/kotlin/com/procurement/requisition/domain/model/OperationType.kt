package com.procurement.requisition.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {

    QUALIFICATION("qualification"),
    QUALIFICATION_CONSIDERATION("qualificationConsideration"),
    QUALIFICATION_DECLARE_NON_CONFLICT_OF_INTEREST("qualificationDeclareNonConflictOfInterest"),
    QUALIFICATION_PROTOCOL("qualificationProtocol");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
