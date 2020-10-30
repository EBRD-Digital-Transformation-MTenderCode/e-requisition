package com.procurement.requisition.infrastructure.handler.v1.get.award.model

import com.procurement.requisition.application.service.get.award.model.GetAwardCriteriaAndConversionsResult
import com.procurement.requisition.infrastructure.handler.converter.asString

fun GetAwardCriteriaAndConversionsResult.convert() = GetAwardCriteriaAndConversionsResponse(

    awardCriteria = awardCriteria.asString(),
    awardCriteriaDetails = awardCriteriaDetails.asString(),
    conversions = conversions
        ?.map { conversion ->
            GetAwardCriteriaAndConversionsResponse.Conversion(
                id = conversion.id.underlying,
                relatesTo = conversion.relatesTo.asString(),
                relatedItem = conversion.relatedItem,
                description = conversion.description,
                rationale = conversion.rationale,
                coefficients = conversion.coefficients
                    .map { coefficient ->
                        GetAwardCriteriaAndConversionsResponse.Conversion.Coefficient(
                            id = coefficient.id.underlying,
                            value = coefficient.value,
                            coefficient = coefficient.coefficient
                        )
                    }
            )
        }
)
