package com.procurement.requisition.application.service.get.tender.currency.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetTenderCurrencyCommand(val cpid: Cpid, val ocid: Ocid)
