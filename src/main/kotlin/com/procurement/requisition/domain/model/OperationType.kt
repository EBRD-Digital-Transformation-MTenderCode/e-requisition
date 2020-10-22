package com.procurement.requisition.domain.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class OperationType(override val key: String) : EnumElementProvider.Element {

    CREATE_PCR("createPcr"),
    TENDER_PERIOD_END_IN_PCR("tenderPeriodEndInPcr"),
    TENDER_PERIOD_END_AUCTION_IN_PCR("tenderPeriodEndAuctionInPcr");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info())
}
