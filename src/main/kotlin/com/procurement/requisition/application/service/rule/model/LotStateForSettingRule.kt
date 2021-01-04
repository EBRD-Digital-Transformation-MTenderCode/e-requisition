package com.procurement.requisition.application.service.rule.model

import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails

class LotStateForSettingRule(
    val status: LotStatus,
    val statusDetails: LotStatusDetails?
)
