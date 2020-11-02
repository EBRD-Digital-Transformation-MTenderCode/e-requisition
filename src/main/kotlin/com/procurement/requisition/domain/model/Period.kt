package com.procurement.requisition.domain.model

import java.time.LocalDateTime

data class Period(
    val endDate: LocalDateTime?,
    val startDate: LocalDateTime?
)
