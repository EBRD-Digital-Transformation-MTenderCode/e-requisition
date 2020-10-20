package com.procurement.requisition.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.lib.EnumElementProvider

enum class TargetRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {

    ITEM("item"),
    LOT("lot");

    override fun toString(): String = key

    companion object : EnumElementProvider<TargetRelatesTo>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CriterionRelatesTo.orThrow(name)
    }
}