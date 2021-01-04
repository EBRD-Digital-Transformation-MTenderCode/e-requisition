package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId

class CheckLotAwardedCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val lotId: LotId
)
