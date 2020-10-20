package com.procurement.requisition.domain.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class CriterionSource(@JsonValue override val key: String) : EnumElementProvider.Key {

    BUYER("buyer"),
    PROCURING_ENTITY("procuringEntity"),
    TENDERER("tenderer");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriterionSource>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
