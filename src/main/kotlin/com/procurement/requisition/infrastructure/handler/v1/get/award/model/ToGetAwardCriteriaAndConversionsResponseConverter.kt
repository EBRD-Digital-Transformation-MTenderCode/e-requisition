package com.procurement.requisition.infrastructure.handler.v1.get.award.model

import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsResult

fun GetAwardCriteriaAndConversionsResult.convert() = GetAwardCriteriaAndConversionsResponse(

    awardCriteria = this.awardCriteria,
    awardCriteriaDetails = this.awardCriteriaDetails,
    conversions = conversions
        ?.map { conversion ->
            GetAwardCriteriaAndConversionsResponse.Conversion(
                id = conversion.id,
                relatesTo = conversion.relatesTo,
                relatedItem = conversion.relatedItem,
                description = conversion.description,
                rationale = conversion.rationale,
                coefficients = conversion.coefficients
                    .map { coefficient ->
                        GetAwardCriteriaAndConversionsResponse.Conversion.Coefficient(
                            id = coefficient.id,
                            value = coefficient.value,
                            coefficient = coefficient.coefficient
                        )
                    }
            )
        }

)