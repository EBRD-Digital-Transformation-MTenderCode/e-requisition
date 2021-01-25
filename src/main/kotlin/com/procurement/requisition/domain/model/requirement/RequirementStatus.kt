package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class RequirementStatus(override val key: String) : EnumElementProvider.Element {

    ACTIVE("active"),
    ;

    override fun toString(): String = key

    companion object : EnumElementProvider<RequirementStatus>(info = info())
}
