package com.procurement.requisition.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {

    CREATE_PCR("createPcr"),
    TENDER_PERIOD_END_IN_PCR("tenderPeriodEndInPcr"),
    TENDER_PERIOD_END_AUCTION_IN_PCR("tenderPeriodEndAuctionInPcr");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
