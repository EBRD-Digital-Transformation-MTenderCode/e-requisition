package com.procurement.requisition.application.service.check.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.lot.LotId

class CheckLotsStatusCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val relatedLot: LotId
)
