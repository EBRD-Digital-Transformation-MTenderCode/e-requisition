package com.procurement.requisition.domain.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class CriterionRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {

    AWARD("award"),
    ITEM("item"),
    LOT("lot"),
    QUALIFICATION("qualification"),
    TENDERER("tenderer");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriterionRelatesTo>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
