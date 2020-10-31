package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class TenderStatusDetails(override val key: String) : EnumElementProvider.Element {
    AGGREGATED("aggregated"),
    AGGREGATION("aggregation"),
    AGGREGATION_PENDING("aggregationPending"),
    AUCTION("auction"),
    AWARDED_CONTRACT_PREPARATION("awardedContractPreparation"),
    AWARDED_STANDSTILL("awardedStandStill"),
    AWARDED_SUSPENDED("awardedSuspended"),
    AWARDING("awarding"),
    CANCELLATION("cancellation"),
    CLARIFICATION("clarification"),
    COMPLETE("complete"),
    EMPTY("empty"),
    EVALUATION("evaluation"),
    LACK_OF_QUALIFICATIONS("lackOfQualifications"),
    LACK_OF_SUBMISSIONS("lackOfSubmissions"),
    NEGOTIATION("negotiation"),
    PLANNED("planned"),
    PLANNING("planning"),
    QUALIFICATION("qualification"),
    QUALIFICATION_STANDSTILL("qualificationStandstill"),
    SUBMISSION("submission"),
    SUSPENDED("suspended"),
    TENDERING("tendering");

    override fun toString(): String = key

    companion object : EnumElementProvider<TenderStatusDetails>(info = info())
}
