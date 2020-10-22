package com.procurement.requisition.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class ProcurementMethodDetails(override val key: String) : EnumElementProvider.Element {

    CD("CD"),
    CF("CF"),
    DA("DA"),
    DC("DC"),
    FA("FA"),
    GPA("GPA"),
    IP("IP"),
    MV("MV"),
    NP("NP"),
    OF("OF"),
    OP("OP"),
    OT("OT"),
    RT("RT"),
    SV("SV"),
    TEST_CD("TEST_CD"),
    TEST_CF("TEST_CF"),
    TEST_DA("TEST_DA"),
    TEST_DC("TEST_DC"),
    TEST_FA("TEST_FA"),
    TEST_GPA("TEST_GPA"),
    TEST_IP("TEST_IP"),
    TEST_MV("TEST_MV"),
    TEST_NP("TEST_NP"),
    TEST_OF("TEST_OF"),
    TEST_OP("TEST_OP"),
    TEST_OT("TEST_OT"),
    TEST_RT("TEST_RT"),
    TEST_SV("TEST_SV");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodDetails>(info = info())
}
