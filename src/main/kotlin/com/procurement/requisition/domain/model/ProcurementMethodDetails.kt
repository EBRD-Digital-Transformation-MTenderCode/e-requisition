package com.procurement.requisition.domain.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class ProcurementMethodDetails(override val key: String, val procurementMethod: String) : EnumElementProvider.Element {

    CD(key = "CD", procurementMethod = "selective"),
    CF(key = "CF", procurementMethod = "selective"),
    DA(key = "DA", procurementMethod = "limited"),
    DC(key = "DC", procurementMethod = "selective"),
    DCO(key = "DCO", procurementMethod = "selective"),
    FA(key = "FA", procurementMethod = "limited"),
    GPA(key = "GPA", procurementMethod = "selective"),
    IP(key = "IP", procurementMethod = "selective"),
    MC(key = "MC", procurementMethod = "selective"),
    MV(key = "MV", procurementMethod = "open"),
    NP(key = "NP", procurementMethod = "limited"),
    OF(key = "OF", procurementMethod = "selective"),
    OP(key = "OP", procurementMethod = "selective"),
    OT(key = "OT", procurementMethod = "open"),
    RFQ(key = "RFQ", procurementMethod = "selective"),
    RT(key = "RT", procurementMethod = "selective"),
    SV(key = "SV", procurementMethod = "open"),
    TEST_CD(key = "TEST_CD", procurementMethod = "selective"),
    TEST_CF(key = "TEST_CF", procurementMethod = "selective"),
    TEST_DA(key = "TEST_DA", procurementMethod = "limited"),
    TEST_DC(key = "TEST_DC", procurementMethod = "selective"),
    TEST_DCO(key = "TEST_DCO", procurementMethod = "selective"),
    TEST_FA(key = "TEST_FA", procurementMethod = "limited"),
    TEST_GPA(key = "TEST_GPA", procurementMethod = "selective"),
    TEST_IP(key = "TEST_IP", procurementMethod = "selective"),
    TEST_MC(key = "TEST_MC", procurementMethod = "selective"),
    TEST_MV(key = "TEST_MV", procurementMethod = "open"),
    TEST_NP(key = "TEST_NP", procurementMethod = "limited"),
    TEST_OF(key = "TEST_OF", procurementMethod = "selective"),
    TEST_OP(key = "TEST_OP", procurementMethod = "selective"),
    TEST_OT(key = "TEST_OT", procurementMethod = "open"),
    TEST_RFQ(key = "TEST_RFQ", procurementMethod = "selective"),
    TEST_RT(key = "TEST_RT", procurementMethod = "selective"),
    TEST_SV(key = "TEST_SV", procurementMethod = "open");

    override fun toString(): String = key

    companion object : EnumElementProvider<ProcurementMethodDetails>(info = info())
}
