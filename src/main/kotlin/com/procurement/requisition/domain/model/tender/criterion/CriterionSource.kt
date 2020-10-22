package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class CriterionSource(override val key: String) : EnumElementProvider.Element {

    BUYER("buyer"),
    PROCURING_ENTITY("procuringEntity"),
    TENDERER("tenderer");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriterionSource>(info = info())
}
