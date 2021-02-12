package com.procurement.requisition.domain.model.relatedprocesses

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class Relationship(override val key: String) : EnumElementProvider.Element {

    FRAMEWORK("framework"),
    PARENT("parent"),
    X_PRE_AWARD_CATALOG_REQUEST("x_preAwardCatalogRequest");

    override fun toString(): String = key

    companion object : EnumElementProvider<Relationship>(info = info())
}
