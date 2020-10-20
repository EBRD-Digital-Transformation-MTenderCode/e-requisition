package com.procurement.requisition.domain.model.award

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class AwardCriteriaDetails(@JsonValue override val key: String) : EnumElementProvider.Key {
    MANUAL("manual"),
    AUTOMATED("automated");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteriaDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardCriteriaDetails.orThrow(name)
    }
}
