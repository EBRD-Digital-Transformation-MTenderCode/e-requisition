package com.procurement.requisition.application.service.validate.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.bid.BidId
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.requirement.response.RequirementResponseId
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.RelatedLots
import java.time.LocalDateTime

data class ValidateRequirementResponsesCommand(
    val cpid: Cpid,
    val ocid: Ocid,

    val tender: Tender?,
    val bids: Bids,
) {

    data class Tender(
        val procurementMethodModalities: List<ProcurementMethodModality>,
    )

    data class Bids(
        val details: List<Detail>
    ) {

        data class Detail(
            val id: BidId,
            val relatedLots: RelatedLots,
            val items: List<Item>,
            val requirementResponses: List<RequirementResponse>,
        ) {

            data class Item(
                val id: ItemId,
            )

            data class RequirementResponse(
                val id: RequirementResponseId,
                val value: DynamicValue,
                val requirement: Requirement,
                val period: Period?,
            ) {

                data class Requirement(
                    val id: RequirementId,
                )

                data class Period(
                    val startDate: LocalDateTime,
                    val endDate: LocalDateTime,
                )
            }
        }
    }
}
