package com.procurement.requisition.application.repository.pcr.model

import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails

class TenderState(val status: TenderStatus, val statusDetails: TenderStatusDetails)
