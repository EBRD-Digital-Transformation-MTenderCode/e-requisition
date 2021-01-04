package com.procurement.requisition.domain.model.tender.criterion

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class CriterionCategory(override val key: String) : EnumElementProvider.Element {

    EXCLUSION("CRITERION.EXCLUSION"),
    SELECTION("CRITERION.SELECTION"),
    OTHER("CRITERION.OTHER");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriterionCategory>(info = info())
}