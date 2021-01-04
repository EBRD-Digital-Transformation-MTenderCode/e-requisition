package com.procurement.requisition.infrastructure.handler.v1.converter

import com.procurement.requisition.application.service.model.result.GetAwardCriteriaAndConversionsResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.v1.model.response.GetAwardCriteriaAndConversionsResponse

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
