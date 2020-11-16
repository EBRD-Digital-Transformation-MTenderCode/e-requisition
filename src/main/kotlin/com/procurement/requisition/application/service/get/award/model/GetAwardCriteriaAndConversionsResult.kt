package com.procurement.requisition.application.service.get.award.model

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.tender.conversion.ConversionId
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientId
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientRate

data class GetAwardCriteriaAndConversionsResult(
    val awardCriteria: AwardCriteria,
    val awardCriteriaDetails: AwardCriteriaDetails,
    val conversions: List<Conversion>?
) {
    data class Conversion(
        val id: ConversionId,
        val relatesTo: ConversionRelatesTo,
        val relatedItem: String,
        val rationale: String,
        val description: String?,

        val coefficients: List<Coefficient>
    ) {
        data class Coefficient(
            val id: CoefficientId,
            val value: DynamicValue,
            val coefficient: CoefficientRate
        )
    }
}
