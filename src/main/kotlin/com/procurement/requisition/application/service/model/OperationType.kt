package com.procurement.requisition.application.service.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class OperationType(override val key: String) : EnumElementProvider.Element {

    AWARD_CONSIDERATION("awardConsideration"),
    COMPLETE_SOURCING("completeSourcing"),
    CREATE_PCR("createPcr"),
    PCR_PROTOCOL("pcrProtocol"),
    SUBMIT_BID_IN_PCR("submitBidInPcr"),
    TENDER_PERIOD_END_AUCTION_IN_PCR("tenderPeriodEndAuctionInPcr"),
    TENDER_PERIOD_END_IN_PCR("tenderPeriodEndInPcr"),
    WITHDRAW_BID("withdrawBid"),
    WITHDRAW_PCR_PROTOCOL("withdrawPcrProtocol");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info())
}
