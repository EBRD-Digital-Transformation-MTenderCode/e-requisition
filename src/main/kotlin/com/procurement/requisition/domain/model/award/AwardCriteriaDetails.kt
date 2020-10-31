package com.procurement.requisition.domain.model.award

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class AwardCriteriaDetails(override val key: String) : EnumElementProvider.Element {
    MANUAL("manual"),
    AUTOMATED("automated");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteriaDetails>(info = info())
}
