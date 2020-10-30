package com.procurement.requisition.application.service.get.award

import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsResult
import com.procurement.requisition.domain.model.tender.Tender

object ToGetAwardCriteriaAndConversionsResultConverter {

    fun fromDomain(tender: Tender): GetAwardCriteriaAndConversionsResult =
        GetAwardCriteriaAndConversionsResult(
            awardCriteria = tender.awardCriteria,
            awardCriteriaDetails = tender.awardCriteriaDetails,
            conversions = tender.conversions
                .map { conversion ->
                    GetAwardCriteriaAndConversionsResult.Conversion(
                        id = conversion.id,
                        relatesTo = conversion.relatesTo,
                        relatedItem = conversion.relatedItem,
                        description = conversion.description,
                        rationale = conversion.rationale,
                        coefficients = conversion.coefficients
                            .map { coefficient ->
                                GetAwardCriteriaAndConversionsResult.Conversion.Coefficient(
                                    id = coefficient.id,
                                    value = coefficient.value,
                                    coefficient = coefficient.coefficient
                                )
                            }
                    )
                }
        )
}
