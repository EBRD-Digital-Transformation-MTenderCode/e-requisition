package com.procurement.requisition.domain.model.relatedprocesses

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class RelatedProcessScheme(override val key: String) : EnumElementProvider.Element {

    OCID("ocid");

    override fun toString(): String = key

    companion object : EnumElementProvider<RelatedProcessScheme>(info = info())
}
