package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.Documents
import com.procurement.requisition.domain.model.tender.conversion.Conversions
import com.procurement.requisition.domain.model.tender.criterion.Criteria
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.domain.model.tender.target.Targets
import java.time.LocalDateTime

data class Tender(
    val id: TenderId,
    val status: TenderStatus,
    val statusDetails: TenderStatusDetails,
    val date: LocalDateTime,
    val title: String,
    val description: String,
    val classification: Classification,
    val lots: Lots,
    val items: Items,
    val targets: Targets,
    val criteria: Criteria,
    val conversions: Conversions,
    val procurementMethodModalities: ProcurementMethodModalities,
    val awardCriteria: AwardCriteria,
    val awardCriteriaDetails: AwardCriteriaDetails,
    val documents: Documents,
    val value: Value,
)
