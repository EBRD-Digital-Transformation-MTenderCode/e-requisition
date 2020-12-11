package com.procurement.requisition.application.repository.rule.model

import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails

class LotStateForSettingsRule(
    val status: LotStatus,
    val statusDetails: LotStatusDetails?
)
