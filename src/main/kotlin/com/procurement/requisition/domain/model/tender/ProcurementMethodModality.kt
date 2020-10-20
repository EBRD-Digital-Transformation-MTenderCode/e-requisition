package com.procurement.requisition.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.EnumElementProvider

enum class ProcurementMethodModality(@JsonValue override val key: String) : EnumElementProvider.Key {
    REQUIRES_ELECTRONIC_CATALOGUE("requiresElectronicCatalogue"),
    ELECTRONIC_AUCTION("electronicAuction");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodModality>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = ProcurementMethodModality.orThrow(name)
    }
}