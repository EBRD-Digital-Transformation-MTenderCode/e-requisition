package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.EntityBase
import java.time.LocalDateTime

data class Requirement(
    override val id: RequirementId,
    val title: String,
    val description: String? = null,
    val period: Period? = null,
    val dataType: DynamicValue.DataType,
    val expectedValue: ExpectedValue? = null,
    val minValue: MinValue? = null,
    val maxValue: MaxValue? = null,
    val eligibleEvidences: List<EligibleEvidence>,
    val status: RequirementStatus?,
    val datePublished: LocalDateTime?
) : EntityBase<RequirementId>() {

    data class Period(
        val startDate: LocalDateTime,
        val endDate: LocalDateTime
    )

    companion object {
        fun hasOnlyExpectedValue(expectedValue: ExpectedValue?, minValue: MinValue?, maxValue: MaxValue?): Boolean =
            expectedValue.isPresent && minValue.isNotPresent && maxValue.isNotPresent

        fun hasOnlyMinValue(expectedValue: ExpectedValue?, minValue: MinValue?, maxValue: MaxValue?): Boolean =
            expectedValue.isNotPresent && minValue.isPresent && maxValue.isNotPresent

        fun hasOnlyMaxValue(expectedValue: ExpectedValue?, minValue: MinValue?, maxValue: MaxValue?): Boolean =
            expectedValue.isNotPresent && minValue.isNotPresent && maxValue.isPresent

        fun hasRangeValue(expectedValue: ExpectedValue?, minValue: MinValue?, maxValue: MaxValue?): Boolean =
            expectedValue.isNotPresent && minValue.isPresent && maxValue.isPresent

        fun valueNotBounded(expectedValue: ExpectedValue?, minValue: MinValue?, maxValue: MaxValue?): Boolean =
            expectedValue.isNotPresent && minValue.isNotPresent && maxValue.isNotPresent
    }
}
