package com.procurement.requisition.domain.model.tender

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class ProcurementMethodModality(override val key: String) : EnumElementProvider.Element {
    REQUIRES_ELECTRONIC_CATALOGUE("requiresElectronicCatalogue"),
    ELECTRONIC_AUCTION("electronicAuction");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodModality>(info = info())
}
