package com.procurement.requisition.application.service.get.lot

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.get.lot.error.GetActiveLotIdsErrors
import com.procurement.requisition.application.service.get.lot.model.ActiveLotIds
import com.procurement.requisition.application.service.get.lot.model.GetActiveLotIdsCommand
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class GetActiveLotsService(
    val pcrRepository: PCRRepository,
    val pcrDeserializer: PCRDeserializer
) {

    fun get(command: GetActiveLotIdsCommand): Result<ActiveLotIds, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return GetActiveLotIdsErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        return pcr.tender.lots.asSequence()
            .filter { lot -> lot.status == LotStatus.ACTIVE }
            .map { lot -> ActiveLotIds.Lot(id = lot.id) }
            .toList()
            .let { lots -> ActiveLotIds(lots) }
            .asSuccess()
    }
}
