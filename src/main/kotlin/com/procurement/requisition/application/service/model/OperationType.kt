package com.procurement.requisition.application.service.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class OperationType(override val key: String) : EnumElementProvider.Element {

    APPLY_CONFIRMATIONS("applyConfirmations"),
    AWARD_CONSIDERATION("awardConsideration"),
    COMPLETE_SOURCING("completeSourcing"),
    CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER("createConfirmationResponseBySupplier"),
    CREATE_PCR("createPcr"),
    CREATE_RFQ("createRfq"),
    NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION("nextStepAfterSuppliersConfirmation"),
    PCR_PROTOCOL("pcrProtocol"),
    SUBMIT_BID_IN_PCR("submitBidInPcr"),
    TENDER_PERIOD_END_AUCTION_IN_PCR("tenderPeriodEndAuctionInPcr"),
    TENDER_PERIOD_END_IN_PCR("tenderPeriodEndInPcr"),
    WITHDRAW_BID("withdrawBid"),
    WITHDRAW_PCR_PROTOCOL("withdrawPcrProtocol");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info())
}
