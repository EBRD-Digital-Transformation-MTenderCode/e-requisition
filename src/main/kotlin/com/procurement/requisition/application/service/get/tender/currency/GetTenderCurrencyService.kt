package com.procurement.requisition.application.service.get.tender.currency

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.get.tender.currency.error.GetTenderCurrencyErrors
import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyCommand
import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import org.springframework.stereotype.Service

@Service
class GetTenderCurrencyService(
    val pcrRepository: PCRRepository,
    val pcrDeserializer: PCRDeserializer
) {

    fun get(command: GetTenderCurrencyCommand): Result<GetTenderCurrencyResult, Failure> {
        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it }
            ?: return GetTenderCurrencyErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid).asFailure()

        val result = GetTenderCurrencyResult(
            tender = GetTenderCurrencyResult.Tender(
                value = GetTenderCurrencyResult.Tender.Value(
                    currency = pcr.tender.value.currency
                )
            )
        )

        return Result.success(result)
    }
}
