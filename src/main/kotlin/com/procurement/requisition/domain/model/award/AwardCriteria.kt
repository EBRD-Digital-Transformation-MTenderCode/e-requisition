package com.procurement.requisition.domain.model.award

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class AwardCriteria(override val key: String) : EnumElementProvider.Element {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteria>(info = info())
}
