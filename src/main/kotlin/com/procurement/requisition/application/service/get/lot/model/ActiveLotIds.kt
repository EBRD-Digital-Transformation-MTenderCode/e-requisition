package com.procurement.requisition.application.service.get.lot.model

import com.procurement.requisition.domain.model.tender.lot.LotId

data class ActiveLotIds(val lots: List<Lot>) {

    data class Lot(val id: LotId)
}
