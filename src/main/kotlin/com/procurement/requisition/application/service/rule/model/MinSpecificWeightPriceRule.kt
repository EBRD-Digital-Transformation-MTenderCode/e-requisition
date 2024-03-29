package com.procurement.requisition.application.service.rule.model

import com.procurement.requisition.domain.model.MainProcurementCategory
import java.math.BigDecimal

data class MinSpecificWeightPriceRule(
    private val goods: BigDecimal,
    private val services: BigDecimal,
    private val works: BigDecimal
) {
    fun valueOf(mainProcurementCategory: MainProcurementCategory): BigDecimal =
        when (mainProcurementCategory) {
            MainProcurementCategory.GOODS -> goods
            MainProcurementCategory.WORKS -> works
            MainProcurementCategory.SERVICES -> services
        }
}
