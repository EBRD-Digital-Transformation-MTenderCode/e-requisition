package com.procurement.requisition.domain.model.award

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class AwardCriteria(@JsonValue override val key: String) : EnumElementProvider.Key {
    PRICE_ONLY("priceOnly"),
    COST_ONLY("costOnly"),
    QUALITY_ONLY("qualityOnly"),
    RATED_CRITERIA("ratedCriteria");

    override fun toString(): String = key

    companion object : EnumElementProvider<AwardCriteria>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = AwardCriteria.orThrow(name)
    }
}