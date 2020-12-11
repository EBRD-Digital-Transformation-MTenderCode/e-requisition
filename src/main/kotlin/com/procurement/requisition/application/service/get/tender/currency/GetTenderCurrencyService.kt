package com.procurement.requisition.application.service.get.tender.currency

import com.procurement.requisition.application.service.PCRManagementService
import com.procurement.requisition.application.service.get.tender.currency.error.GetTenderCurrencyErrors
import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyCommand
import com.procurement.requisition.application.service.get.tender.currency.model.GetTenderCurrencyResult
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import org.springframework.stereotype.Service

@Service
class GetTenderCurrencyService(
    private val pcrManagement: PCRManagementService,
) {

    fun get(command: GetTenderCurrencyCommand): Result<GetTenderCurrencyResult, Failure> {
        val pcr = pcrManagement.find(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it }
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
