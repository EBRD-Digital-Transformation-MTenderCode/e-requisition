package com.procurement.requisition.domain.model.requirement

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class EligibleEvidenceType(override val key: String) : EnumElementProvider.Element {

    REFERENCE("reference"),
    DOCUMENT("document");

    override fun toString(): String = key

    companion object : EnumElementProvider<EligibleEvidenceType>(info = info())
}
