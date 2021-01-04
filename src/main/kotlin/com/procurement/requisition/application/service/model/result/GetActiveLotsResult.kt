package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.tender.lot.LotId

data class GetActiveLotsResult(val lots: List<Lot>) {

    data class Lot(val id: LotId)
}
